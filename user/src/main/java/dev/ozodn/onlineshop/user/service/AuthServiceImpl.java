package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.DuplicateResourceException;
import dev.ozodn.onlineshop.user.config.JwtProperties;
import dev.ozodn.onlineshop.user.dto.AuthResponse;
import dev.ozodn.onlineshop.user.dto.LoginRequest;
import dev.ozodn.onlineshop.user.dto.RefreshTokenRequest;
import dev.ozodn.onlineshop.user.dto.RegisterRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;
import dev.ozodn.onlineshop.user.entity.RefreshToken;
import dev.ozodn.onlineshop.user.entity.Role;
import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.mapper.UserMapper;
import dev.ozodn.onlineshop.user.repository.RefreshTokenRepository;
import dev.ozodn.onlineshop.user.repository.RoleRepository;
import dev.ozodn.onlineshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


/**
 * Service implementation of {@link AuthService} handling user registration and authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "CUSTOMER";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";
    private static final String INVALID_REFRESH_TOKEN_MESSAGE = "Invalid or expired refresh token";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    @Override
    public UserResponse register(@NonNull RegisterRequest request) {
        validateEmailAvailability(request.email());

        User user = buildUser(request);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getExternalId());

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Override
    public AuthResponse login(@NonNull LoginRequest request) {
        User user = findActiveUser(request.email());

        validatePassword(request.password(), user.getPasswordHash());

        return generateAuthResponse(user);
    }

    @Transactional
    @Override
    public AuthResponse refresh(@NonNull RefreshTokenRequest request) {
        RefreshToken refreshToken = findValidRefreshToken(request.refreshToken());
        User user = getActiveUser(refreshToken);

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.debug("Refresh token rotated for user {}", user.getExternalId());

        return generateAuthResponse(user);
    }

    @Transactional
    @Override
    public void logout(@NonNull RefreshTokenRequest request) {
        if (!StringUtils.hasText(request.refreshToken())) {
            return;
        }

        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(this::revokeRefreshToken);
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", "email", email);
        }
    }

    private User buildUser(@NonNull RegisterRequest request) {
        Role customerRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException(
                        "Default role '" + DEFAULT_ROLE + "' is missing from database"
                ));

        return User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .roles(new HashSet<>(Set.of(customerRole)))
                .build();
    }

    private @NonNull User findActiveUser(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));
    }

    private void validatePassword(String rawPassword, String passwordHash) {
        if (!passwordEncoder.matches(rawPassword, passwordHash)) {
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }
    }

    private @NonNull RefreshToken findValidRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException(INVALID_REFRESH_TOKEN_MESSAGE));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException(INVALID_REFRESH_TOKEN_MESSAGE);
        }

        return refreshToken;
    }

    private @NonNull User getActiveUser(@NonNull RefreshToken refreshToken) {
        User user = refreshToken.getUser();

        if (user == null || user.isDeleted()) {
            throw new BadCredentialsException(INVALID_REFRESH_TOKEN_MESSAGE);
        }

        return user;
    }

    private @NonNull AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        long expiresIn = jwtProperties.getAccessTokenExpirationMs() / 1000;

        return new AuthResponse(accessToken, refreshToken, expiresIn);
    }

    private void revokeRefreshToken(@NonNull RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();

        if (user != null) {
            log.debug(
                    "Revoked refresh token on logout for user {}",
                    user.getExternalId()
            );
        }
    }
}