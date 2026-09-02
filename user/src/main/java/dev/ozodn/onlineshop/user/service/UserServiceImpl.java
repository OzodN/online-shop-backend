package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.ResourceNotFoundException;
import dev.ozodn.onlineshop.user.dto.UpdateProfileRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;
import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.mapper.UserMapper;
import dev.ozodn.onlineshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserService} managing user profile retrieval and updates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

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

    private @NonNull User findUser(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "externalId", externalId)
                );
    }
}
