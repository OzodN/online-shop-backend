package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.common.exception.DuplicateResourceException;
import dev.ozodn.onlineshop.user.dto.*;
import org.springframework.security.authentication.BadCredentialsException;

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

    /**
     * Authenticates user credentials and issues access and refresh tokens.
     *
     * @param request login payload containing user credentials
     * @return authentication response containing access and refresh tokens
     * @throws BadCredentialsException if credentials are invalid or user account is deleted
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refreshes user authentication tokens using a valid refresh token.
     *
     * @param request payload containing the current refresh token
     * @return authentication response containing new access and refresh tokens
     * @throws BadCredentialsException if the refresh token is invalid, expired, or revoked
     */
    AuthResponse refresh(RefreshTokenRequest request);

    /**
     * Revokes the provided refresh token to log out the user.
     *
     * @param request payload containing the refresh token to revoke
     */
    void logout(RefreshTokenRequest request);
}

