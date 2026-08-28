package dev.ozodn.onlineshop.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an authenticated user attempts an operation they are not authorized to perform.
 */
public class ForbiddenOperationException extends BaseException {

    /**
     * Constructs a forbidden operation exception with a specific error message.
     *
     * @param message detail message explaining why the operation is forbidden
     */
    public ForbiddenOperationException(String message) {
        super(message, HttpStatus.FORBIDDEN, "Access Forbidden", "access-forbidden");
    }
}
