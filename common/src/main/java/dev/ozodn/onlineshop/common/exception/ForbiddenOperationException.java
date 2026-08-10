package dev.ozodn.onlineshop.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends BaseException {
    public ForbiddenOperationException(String message) {
        super(message, HttpStatus.FORBIDDEN, "Access Forbidden", "access-forbidden");
    }
}
