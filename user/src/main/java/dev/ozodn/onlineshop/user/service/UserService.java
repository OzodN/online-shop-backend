package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.ResourceNotFoundException;
import dev.ozodn.onlineshop.user.dto.UpdateProfileRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;

import java.util.UUID;

/**
 * Service interface defining user profile management operations.
 */
public interface UserService {

    /**
     * Retrieves the profile for the specified user identifier.
     *
     * @param externalId unique business identifier of the user
     * @return user profile response containing account details
     * @throws ResourceNotFoundException if no user is found with the given external identifier
     */
    UserResponse getProfile(UUID externalId);

    /**
     * Updates personal profile information for the specified user.
     *
     * @param externalId unique business identifier of the user to update
     * @param request payload containing updated profile details
     * @return updated user profile response
     * @throws ResourceNotFoundException if no user is found with the given external identifier
     */
    UserResponse updateProfile(UUID externalId, UpdateProfileRequest request);
}
