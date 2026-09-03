package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.DuplicateResourceException;
import dev.ozodn.onlineshop.common.exception.ResourceNotFoundException;
import dev.ozodn.onlineshop.user.dto.UpdateProfileRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;
import dev.ozodn.onlineshop.user.entity.Role;
import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.mapper.UserMapper;
import dev.ozodn.onlineshop.user.repository.RoleRepository;
import dev.ozodn.onlineshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserService} managing user profile retrieval and updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final String SELLER_ROLE = "SELLER";

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID externalId) {
        return userMapper.toResponse(findUser(externalId));
    }


    @Override
    public UserResponse updateProfile(UUID externalId, UpdateProfileRequest request) {
        User user = findUser(externalId);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse becomeSeller(UUID externalId) {
        User user = findUser(externalId);

        if (hasRole(user, SELLER_ROLE)) {
            throw new DuplicateResourceException("User", "role", SELLER_ROLE);
        }

        Role role = findRole(SELLER_ROLE);
        user.addRole(role);

        log.info("User {} became a SELLER", user.getExternalId());

        return userMapper.toResponse(user);
    }

    private boolean hasRole(@NonNull User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    private @NonNull Role findRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Role '%s' not found".formatted(roleName))
                );
    }

    private @NonNull User findUser(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "externalId", externalId)
                );
    }
}
