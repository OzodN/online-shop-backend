package dev.ozodn.onlineshop.user.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter @Setter
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Base64-encoded secret key (minimum 256-bit for HMAC-SHA256).
     */
    @NotBlank(message = "JWT secret must not be blank")
    private String secret;

    /**
     * Access token validity in milliseconds (default: 15 min).
     */
    @Positive(message = "Access token expiration must be positive")
    private long accessTokenExpirationMs = 900000L;

    /**
     * Refresh token validity in milliseconds (default: 7 days).
     */
    @Positive(message = "Refresh token expiration must be positive")
    private long refreshTokenExpirationMs = 604800000L;
}
