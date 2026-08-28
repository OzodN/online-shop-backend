package dev.ozodn.onlineshop.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Translates application and framework exceptions into RFC 7807 {@link ProblemDetail} HTTP responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom business exceptions extending BaseException.
     */
    @ExceptionHandler(BaseException.class)
    public ProblemDetail handleBaseException(@NonNull BaseException ex, @NonNull HttpServletRequest request) {
        log.warn("Business exception occurred [{}: {}] path: {}",
                ex.getClass().getSimpleName(), ex.getMessage(), request.getRequestURI());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        problemDetail.setTitle(ex.getTitle());
        problemDetail.setType(ex.getType());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles DTO validation errors (@Valid on request bodies).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpServletRequest request) {
        log.warn("Validation failed for request to {}: {} error(s)",
                request.getRequestURI(), ex.getBindingResult().getErrorCount());

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for request parameters");
        problemDetail.setTitle("Validation Failure");
        problemDetail.setType(URI.create("https://onlineshop.dev/errors/validation-error"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("invalidFields", fieldErrors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles path/query parameter constraint validation errors (@Validated).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(@NonNull ConstraintViolationException ex, @NonNull HttpServletRequest request) {
        log.warn("Constraint violation for request to {}: {} violation(s)",
                request.getRequestURI(), ex.getConstraintViolations().size());

        Map<String, String> invalidFields = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            // Extract leaf parameter name (e.g. "registerUser.email" -> "email")
                            return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                        },
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Constraint validation failed for request parameters"
        );
        problemDetail.setTitle("Constraint Violation");
        problemDetail.setType(URI.create("https://onlineshop.dev/errors/constraint-violation"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("invalidFields", invalidFields);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles malformed or unparseable JSON payloads.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpServletRequest request) {
        log.warn("Malformed JSON request to {}: {}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request body or invalid data format"
        );
        problemDetail.setTitle("Malformed JSON");
        problemDetail.setType(URI.create("https://onlineshop.dev/errors/malformed-json"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Catch-all handler for unexpected internal server errors (500).
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnhandledException(Exception ex, @NonNull HttpServletRequest request) {
        log.error("Unhandled exception occurred while processing request to {}", request.getRequestURI(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected internal error occurred. Please contact support if the issue persists."
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://onlineshop.dev/errors/internal-server-error"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handles Spring Security framework authorization failures (@PreAuthorize / method security).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleSpringSecurityAccessDenied(@NonNull AccessDeniedException ex, @NonNull HttpServletRequest request) {
        log.warn("Spring Security access denied for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have sufficient permissions to access this resource"
        );
        problemDetail.setTitle("Access Denied");
        problemDetail.setType(URI.create("https://onlineshop.dev/errors/access-denied"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}