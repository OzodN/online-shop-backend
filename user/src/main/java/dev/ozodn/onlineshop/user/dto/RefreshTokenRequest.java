package dev.ozodn.onlineshop.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @Schema(description = "Refresh token used to obtain a new access token or revoke session", example = "d3b07384-d113-4672-8889-7cfc941c4103")
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}