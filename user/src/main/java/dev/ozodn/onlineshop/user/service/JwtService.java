package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.user.entity.User;
import io.jsonwebtoken.JwtException;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for generating, extracting, and validating JSON Web Tokens (JWT).
 */
public interface JwtService {

    /**
     * Generates a signed JWT access token containing user identity and role claims.
     *
     * @param user user entity containing identity and roles
     * @return signed JWT access token string
     */
    String generateAccessToken(User user);

    /**
     * Generates and persists a new refresh token for the specified user.
     *
     * @param user user entity for which to create the refresh token
     * @return generated refresh token string
     */
    String generateRefreshToken(User user);

    /**
     * Extracts the user external identifier from the token subject claim.
     *
     * @param token JWT token string
     * @return external identifier of the user
     * @throws JwtException if the subject claim is missing or not a valid UUID
     */
    UUID extractExternalId(String token);

    /**
     * Extracts the user email address from the token claims.
     *
     * @param token JWT token string
     * @return user email address stored in the token
     */
    String extractEmail(String token);

    /**
     * Extracts the user identifier, prioritizing external ID and falling back to email.
     *
     * @param token JWT token string
     * @return optional containing the user identifier if found
     */
    Optional<String> extractIdentifier(String token);

    /**
     * Validates the signature, structure, and expiration of the JWT token.
     *
     * @param token JWT token string to validate
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    boolean isTokenValid(String token);
}
