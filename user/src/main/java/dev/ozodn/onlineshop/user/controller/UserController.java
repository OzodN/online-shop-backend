package dev.ozodn.onlineshop.user.controller;

import dev.ozodn.onlineshop.common.security.CurrentUserProvider;
import dev.ozodn.onlineshop.user.dto.UpdateProfileRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;
import dev.ozodn.onlineshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for user profile management endpoints.
 */
@Tag(name = "Users", description = "User profile management endpoints")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Get current user profile", description = "Retrieves the profile information of the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(
                userService.getProfile(currentUserProvider.getCurrentUserExternalId())
        );
    }

    @Operation(summary = "Update current user profile", description = "Updates the profile information of the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "User profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(
                userService.updateProfile(
                        currentUserProvider.getCurrentUserExternalId(),
                        request
                )
        );
    }

    @Operation(summary = "Become a seller", description = "Adds the SELLER role to the currently authenticated user while preserving existing roles.\n After successful promotion, refresh the authentication token to obtain a new JWT containing the updated roles claim.")
    @ApiResponse(responseCode = "200", description = "Successfully elevated to seller")
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Insufficient permissions (requires CUSTOMER role)", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "User already has the SELLER role", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/me/become-seller")
    public ResponseEntity<UserResponse> becomeSeller() {
        return ResponseEntity.ok(
                userService.becomeSeller(currentUserProvider.getCurrentUserExternalId()));
    }
}
