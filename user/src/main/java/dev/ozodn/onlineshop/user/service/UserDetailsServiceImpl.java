package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Loads user-specific data for Spring Security authentication using email or external identifier.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = findUserByIdentifier(identifier)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with identifier: " + identifier
                        )
                );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getExternalId().toString())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .disabled(user.isDeleted())
                .accountLocked(user.isDeleted())
                .build();
    }


    private @NonNull Optional<User> findUserByIdentifier(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> findUserByExternalId(identifier));
    }

    private Optional<User> findUserByExternalId(String identifier) {
        try {
            return userRepository.findByExternalId(UUID.fromString(identifier));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static @NonNull Set<SimpleGrantedAuthority> getAuthorities(@NonNull User user) {
        return user.getRoles().stream()
                .map(role ->
                        new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()))
                .collect(Collectors.toSet());
    }
}