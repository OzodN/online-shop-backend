package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.user.entity.Role;
import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService =
                new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_shouldLoadUserByEmail() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);
        Role customerRole = mock(Role.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getPasswordHash()).thenReturn("encoded-password");
        when(user.getRoles()).thenReturn(Set.of(customerRole));
        when(user.isDeleted()).thenReturn(false);

        when(customerRole.getName()).thenReturn("CUSTOMER");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername(
                        "john@example.com"
                );

        assertEquals(
                externalId.toString(),
                result.getUsername()
        );

        assertEquals(
                "encoded-password",
                result.getPassword()
        );

        assertTrue(
                result.getAuthorities().stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_CUSTOMER")
                        )
        );

        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonLocked());

        verify(userRepository).findByEmail("john@example.com");
        verify(userRepository, never()).findByExternalId(any());
    }

    @Test
    void loadUserByUsername_shouldFallbackToExternalId() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getPasswordHash()).thenReturn("encoded");
        when(user.getRoles()).thenReturn(new HashSet<>());
        when(user.isDeleted()).thenReturn(false);

        when(userRepository.findByEmail(externalId.toString()))
                .thenReturn(Optional.empty());

        when(userRepository.findByExternalId(externalId))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername(
                        externalId.toString()
                );

        assertEquals(
                externalId.toString(),
                result.getUsername()
        );

        verify(userRepository)
                .findByEmail(externalId.toString());

        verify(userRepository)
                .findByExternalId(externalId);
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () ->
                        userDetailsService.loadUserByUsername(
                                "unknown"
                        )
        );
    }

    @Test
    void loadUserByUsername_shouldCreateSellerAuthority() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);
        Role customerRole = mock(Role.class);
        Role sellerRole = mock(Role.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getPasswordHash()).thenReturn("encoded");
        when(user.isDeleted()).thenReturn(false);
        when(user.getRoles())
                .thenReturn(Set.of(customerRole, sellerRole));

        when(customerRole.getName()).thenReturn("CUSTOMER");
        when(sellerRole.getName()).thenReturn("SELLER");

        when(userRepository.findByEmail("seller@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername(
                        "seller@example.com"
                );

        assertEquals(2, result.getAuthorities().size());

        assertTrue(
                result.getAuthorities().stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_CUSTOMER")
                        )
        );

        assertTrue(
                result.getAuthorities().stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_SELLER")
                        )
        );
    }

    @Test
    void loadUserByUsername_shouldDisableDeletedUser() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getPasswordHash()).thenReturn("encoded");
        when(user.getRoles()).thenReturn(new HashSet<>());
        when(user.isDeleted()).thenReturn(true);

        when(userRepository.findByEmail("deleted@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername(
                        "deleted@example.com"
                );

        assertFalse(result.isEnabled());
        assertFalse(result.isAccountNonLocked());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenIdentifierIsNotEmailOrUuid() {
        when(userRepository.findByEmail("not-an-email-or-uuid"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(
                        "not-an-email-or-uuid"
                )
        );

        verify(userRepository)
                .findByEmail("not-an-email-or-uuid");

        verify(userRepository, never())
                .findByExternalId(any(UUID.class));
    }
}