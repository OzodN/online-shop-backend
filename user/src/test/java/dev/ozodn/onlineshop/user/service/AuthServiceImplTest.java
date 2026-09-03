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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                roleRepository,
                refreshTokenRepository,
                userMapper,
                passwordEncoder,
                jwtService,
                jwtProperties
        );
    }

    @Test
    void register_shouldCreateCustomerAndReturnResponse() {
        RegisterRequest request = mock(RegisterRequest.class);
        Role customerRole = mock(Role.class);
        User savedUser = mock(User.class);
        UserResponse expectedResponse = mock(UserResponse.class);

        when(request.email()).thenReturn("john@example.com");
        when(request.password()).thenReturn("password");
        when(request.firstName()).thenReturn("John");
        when(request.lastName()).thenReturn("Doe");
        when(request.phone()).thenReturn("+998901234567");

        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);
        when(roleRepository.findByName("CUSTOMER"))
                .thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("password"))
                .thenReturn("encoded-password");
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);
        when(userMapper.toResponse(savedUser))
                .thenReturn(expectedResponse);

        UserResponse actual =
                authService.register(request);

        assertSame(expectedResponse, actual);

        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByName("CUSTOMER");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExists() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.email()).thenReturn("john@example.com");
        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_shouldThrow_whenCustomerRoleIsMissing() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.email()).thenReturn("john@example.com");
        when(userRepository.existsByEmail("john@example.com"))
                .thenReturn(false);
        when(roleRepository.findByName("CUSTOMER"))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Default role 'CUSTOMER' is missing from database",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        LoginRequest request = mock(LoginRequest.class);
        User user = mock(User.class);
        AuthResponse expectedResponse =
                new AuthResponse("access", "refresh", 900L);

        when(request.email()).thenReturn("john@example.com");
        when(request.password()).thenReturn("password");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("encoded-password");

        when(passwordEncoder.matches(
                "password",
                "encoded-password"
        )).thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("access");
        when(jwtService.generateRefreshToken(user))
                .thenReturn("refresh");
        when(jwtProperties.getAccessTokenExpirationMs())
                .thenReturn(900_000L);

        AuthResponse actual =
                authService.login(request);

        assertEquals(expectedResponse, actual);

        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void login_shouldThrow_whenUserDoesNotExist() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.email()).thenReturn("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void login_shouldThrow_whenPasswordIsInvalid() {
        LoginRequest request = mock(LoginRequest.class);
        User user = mock(User.class);

        when(request.email()).thenReturn("john@example.com");
        when(request.password()).thenReturn("wrong");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("encoded");

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void refresh_shouldRotateTokenAndGenerateNewAuthResponse() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshToken refreshToken = mock(RefreshToken.class);
        User user = mock(User.class);

        AuthResponse expectedResponse =
                new AuthResponse("new-access", "new-refresh", 900L);

        when(request.refreshToken()).thenReturn("old-refresh");
        when(refreshTokenRepository.findByToken("old-refresh"))
                .thenReturn(Optional.of(refreshToken));

        when(refreshToken.isRevoked()).thenReturn(false);
        when(refreshToken.getExpiresAt())
                .thenReturn(Instant.now().plusSeconds(60));
        when(refreshToken.getUser()).thenReturn(user);
        when(user.isDeleted()).thenReturn(false);

        when(jwtService.generateAccessToken(user))
                .thenReturn("new-access");
        when(jwtService.generateRefreshToken(user))
                .thenReturn("new-refresh");
        when(jwtProperties.getAccessTokenExpirationMs())
                .thenReturn(900_000L);

        AuthResponse actual =
                authService.refresh(request);

        assertEquals(expectedResponse, actual);

        verify(refreshToken).setRevoked(true);
        verify(refreshTokenRepository).save(refreshToken);

        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void refresh_shouldThrow_whenTokenDoesNotExist() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);

        when(request.refreshToken()).thenReturn("invalid");
        when(refreshTokenRepository.findByToken("invalid"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.refresh(request)
        );

        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void refresh_shouldThrow_whenTokenIsRevoked() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        when(request.refreshToken()).thenReturn("revoked");
        when(refreshTokenRepository.findByToken("revoked"))
                .thenReturn(Optional.of(refreshToken));
        when(refreshToken.isRevoked()).thenReturn(true);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.refresh(request)
        );

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void refresh_shouldThrow_whenTokenIsExpired() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        when(request.refreshToken()).thenReturn("expired");
        when(refreshTokenRepository.findByToken("expired"))
                .thenReturn(Optional.of(refreshToken));
        when(refreshToken.isRevoked()).thenReturn(false);
        when(refreshToken.getExpiresAt())
                .thenReturn(Instant.now().minusSeconds(1));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.refresh(request)
        );

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void refresh_shouldThrow_whenUserIsDeleted() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshToken refreshToken = mock(RefreshToken.class);
        User user = mock(User.class);

        when(request.refreshToken()).thenReturn("token");
        when(refreshTokenRepository.findByToken("token"))
                .thenReturn(Optional.of(refreshToken));
        when(refreshToken.isRevoked()).thenReturn(false);
        when(refreshToken.getExpiresAt())
                .thenReturn(Instant.now().plusSeconds(60));
        when(refreshToken.getUser()).thenReturn(user);
        when(user.isDeleted()).thenReturn(true);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.refresh(request)
        );

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void logout_shouldRevokeExistingRefreshToken() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        when(request.refreshToken()).thenReturn("refresh");
        when(refreshTokenRepository.findByToken("refresh"))
                .thenReturn(Optional.of(refreshToken));

        authService.logout(request);

        verify(refreshToken).setRevoked(true);
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void logout_shouldDoNothing_whenRefreshTokenIsBlank() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);

        when(request.refreshToken()).thenReturn(" ");

        authService.logout(request);

        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void logout_shouldDoNothing_whenRefreshTokenDoesNotExist() {
        RefreshTokenRequest request = mock(RefreshTokenRequest.class);

        when(request.refreshToken()).thenReturn("unknown");
        when(refreshTokenRepository.findByToken("unknown"))
                .thenReturn(Optional.empty());

        authService.logout(request);

        verify(refreshTokenRepository).findByToken("unknown");
        verify(refreshTokenRepository, never()).save(any());
    }
}