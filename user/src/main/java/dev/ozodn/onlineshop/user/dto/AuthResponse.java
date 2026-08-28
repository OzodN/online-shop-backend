package dev.ozodn.onlineshop.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Refresh token for obtaining new access tokens", example = "d3b07384-d113-4672-8889-7cfc941c4103")
        String refreshToken,

        @Schema(description = "Access token validity in seconds", example = "900")
        Long expiresIn
) {
}
