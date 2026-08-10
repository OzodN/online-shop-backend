package dev.ozodn.onlineshop.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s with %s '%s' already exists", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT,
                "Duplicate " + resourceName,
                "duplicate-" + resourceName.toLowerCase().replace(" ", "-")
        );
    }
}
