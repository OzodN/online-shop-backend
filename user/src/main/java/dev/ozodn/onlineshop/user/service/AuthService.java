package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.DuplicateResourceException;
import dev.ozodn.onlineshop.user.dto.RegisterRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;

/**
 * Service interface defining user authentication and registration operations.
 */
public interface AuthService {

    /**
     * Registers a new user account with default customer privileges.
     *
     * @param request user registration payload containing account details
     * @return response details for the registered user
     * @throws DuplicateResourceException if the email is already registered
     */
    UserResponse register(RegisterRequest request);
}
