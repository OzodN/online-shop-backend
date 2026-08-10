package dev.ozodn.onlineshop.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s with %s '%s' was not found", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                resourceName + " Not Found",
                resourceName.toLowerCase().replace(" ", "-") + "-not-found"
        );
    }

    public ResourceNotFoundException(String resourceName, UUID id) {
        this(resourceName, "ID", id);
    }
}