package dev.ozodn.onlineshop.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumerates standardized error type paths for API problem detail responses.
 */
@Getter
@RequiredArgsConstructor
public enum ApiErrorType {

    UNAUTHORIZED("unauthorized"),
    ACCESS_DENIED("access-denied"),
    VALIDATION_ERROR("validation-error"),
    CONSTRAINT_VIOLATION("constraint-violation"),
    MALFORMED_JSON("malformed-json"),
    INTERNAL_SERVER_ERROR("internal-server-error");

    private final String path;
}