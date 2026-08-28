package dev.ozodn.onlineshop.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        @Schema(description = "Unique public identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID externalId,
        String email,
        String firstName,
        String lastName,
        String phone,
        @Schema(description = "Assigned user roles", example = "[\"CUSTOMER\"]")
        Set<String> roles,
        Instant createdAt
){
}
