package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.DuplicateResourceException;
import dev.ozodn.onlineshop.common.exception.ResourceNotFoundException;
import dev.ozodn.onlineshop.user.dto.UserResponse;
import dev.ozodn.onlineshop.user.entity.Role;
import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.mapper.UserMapper;
import dev.ozodn.onlineshop.user.repository.RoleRepository;
import dev.ozodn.onlineshop.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void becomeSeller_shouldAddSellerRole() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);
        Role sellerRole = mock(Role.class);

        when(userRepository.findByExternalId(externalId))
                .thenReturn(Optional.of(user));

        when(user.getRoles())
                .thenReturn(new HashSet<>());

        when(roleRepository.findByName("SELLER"))
                .thenReturn(Optional.of(sellerRole));

        UserResponse response = mock(UserResponse.class);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.becomeSeller(externalId);

        verify(user).addRole(sellerRole);
        verify(userMapper).toResponse(user);

        assertSame(response, result);
    }

    @Test
    void becomeSeller_shouldThrowWhenUserAlreadySeller() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);
        Role sellerRole = mock(Role.class);

        when(userRepository.findByExternalId(externalId))
                .thenReturn(Optional.of(user));

        when(user.getRoles())
                .thenReturn(Set.of(sellerRole));

        when(sellerRole.getName())
                .thenReturn("SELLER");

        assertThrows(
                DuplicateResourceException.class,
                () -> userService.becomeSeller(externalId)
        );

        verifyNoInteractions(roleRepository);
        verify(userRepository, never()).save(any());
    }

    @Test
    void becomeSeller_shouldThrowWhenUserNotFound() {
        UUID externalId = UUID.randomUUID();

        when(userRepository.findByExternalId(externalId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.becomeSeller(externalId)
        );

        verifyNoInteractions(roleRepository);
    }

    @Test
    void becomeSeller_shouldThrowWhenSellerRoleMissing() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);

        when(userRepository.findByExternalId(externalId))
                .thenReturn(Optional.of(user));

        when(user.getRoles())
                .thenReturn(new HashSet<>());

        when(roleRepository.findByName("SELLER"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () -> userService.becomeSeller(externalId)
        );

        verify(userRepository, never()).save(any());
    }
}