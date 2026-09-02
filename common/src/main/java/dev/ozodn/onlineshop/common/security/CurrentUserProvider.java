package dev.ozodn.onlineshop.common.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Provides access to the currently authenticated user's security context information.
 */
@Component
public class CurrentUserProvider {

    /**
     * Retrieves the external identifier of the currently authenticated user.
     *
     * @return unique business identifier of the authenticated user
     * @throws AuthenticationCredentialsNotFoundException if no user is authenticated or the principal is invalid
     */
    public UUID getCurrentUserExternalId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user is required");
        }

        if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user principal is invalid");
        }

        try {
            return UUID.fromString(userDetails.getUsername());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authenticated user externalId is invalid",
                    exception
            );
        }
    }
}