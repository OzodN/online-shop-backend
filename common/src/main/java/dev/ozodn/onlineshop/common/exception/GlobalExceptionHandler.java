package dev.ozodn.onlineshop.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Intercepts unhandled exceptions across controllers and formats them as problem details.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ProblemDetailFactory problemDetailFactory;

    /**
     * Constructs a global exception handler with the problem detail factory.
     *
     * @param problemDetailFactory factory used to build problem detail responses
     */
    public GlobalExceptionHandler(ProblemDetailFactory problemDetailFactory) {
        this.problemDetailFactory = problemDetailFactory;
    }

    /**
     * Handles domain base exceptions and translates them to problem details.
     *
     * @param ex domain exception that occurred
     * @param request current HTTP request providing the instance URI
     * @return problem detail containing the exception attributes
     */
    @ExceptionHandler(BaseException.class)
    public ProblemDetail handleBaseException(BaseException ex, HttpServletRequest request) {
        log.warn(
                "Business exception [{}] at {}: {}",
                ex.getClass().getSimpleName(),
                request.getRequestURI(),
                ex.getMessage()
        );

        return problemDetailFactory.fromBaseException(
                ex,
                request
        );
    }

    /**
     * Handles controller method argument validation failures.
     *
     * @param ex validation exception containing field errors
     * @param request current HTTP request providing the instance URI
     * @return problem detail containing field validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn(
                "Validation failed for {}: {} error(s)",
                request.getRequestURI(),
                ex.getBindingResult().getErrorCount()
        );

        Map<String, String> invalidFields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value",
                        (existing, replacement) -> existing
                ));

        ProblemDetail problemDetail = problemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "Validation Failure",
                "Validation failed for request parameters",
                ApiErrorType.VALIDATION_ERROR,
                request
        );

        problemDetail.setProperty(
                "invalidFields",
                invalidFields
        );

        return problemDetail;
    }

    /**
     * Handles bean validation constraint violations on method parameters.
     *
     * @param ex constraint violation exception containing violations
     * @param request current HTTP request providing the instance URI
     * @return problem detail containing invalid parameter mappings
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn(
                "Constraint violation for {}: {} violation(s)",
                request.getRequestURI(),
                ex.getConstraintViolations().size()
        );

        Map<String, String> invalidFields = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        this::extractFieldName,
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        ProblemDetail problemDetail = problemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "Constraint Violation",
                "Constraint validation failed for request parameters",
                ApiErrorType.CONSTRAINT_VIOLATION,
                request
        );

        problemDetail.setProperty(
                "invalidFields",
                invalidFields
        );

        return problemDetail;
    }

    /**
     * Handles unreadable or malformed HTTP request bodies.
     *
     * @param ex exception indicating unreadable or malformed payload
     * @param request current HTTP request providing the instance URI
     * @return problem detail indicating malformed JSON format
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn(
                "Malformed JSON request to {}: {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return problemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON",
                "Malformed JSON request body or invalid data format",
                ApiErrorType.MALFORMED_JSON,
                request
        );
    }

    /**
     * Handles authentication failures for unauthenticated requests.
     *
     * @param ex authentication exception that occurred
     * @param request current HTTP request providing the instance URI
     * @return problem detail with unauthorized status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn(
                "Authentication failure for request to {}: {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        String detail = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage()
                : "Authentication failed";

        return problemDetailFactory.create(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                detail,
                ApiErrorType.UNAUTHORIZED,
                request
        );
    }

    /**
     * Handles authorization failures when access to a resource is denied.
     *
     * @param ex access denied exception indicating insufficient permissions
     * @param request current HTTP request providing the instance URI
     * @return problem detail with forbidden status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn(
                "Access denied for request to {}: {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return problemDetailFactory.create(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You do not have sufficient permissions to access this resource",
                ApiErrorType.ACCESS_DENIED,
                request
        );
    }

    /**
     * Handles unexpected runtime exceptions not caught by specific handlers.
     *
     * @param ex unhandled exception that occurred
     * @param request current HTTP request providing the instance URI
     * @return problem detail with internal server error status
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnhandledException(Exception ex, HttpServletRequest request) {
        log.error(
                "Unhandled exception while processing request to {}",
                request.getRequestURI(),
                ex
        );

        return problemDetailFactory.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected internal error occurred. Please contact support if the issue persists.",
                ApiErrorType.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int separatorIndex = path.lastIndexOf('.');

        return separatorIndex >= 0
                ? path.substring(separatorIndex + 1)
                : path;
    }
}