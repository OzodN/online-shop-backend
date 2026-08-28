package dev.ozodn.onlineshop.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Thrown when a requested entity or resource cannot be found in the system.
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Constructs a resource not found exception formatted with the lookup field and value.
     *
     * @param resourceName name of the missing entity or resource
     * @param fieldName name of the field used for the search
     * @param fieldValue value that could not be found
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s with %s '%s' was not found", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                resourceName + " Not Found",
                resourceName.toLowerCase().replace(" ", "-") + "-not-found"
        );
    }

    /**
     * Constructs a resource not found exception for a resource identified by UUID.
     *
     * @param resourceName name of the missing entity or resource
     * @param id unique identifier that was not found
     */
    public ResourceNotFoundException(String resourceName, UUID id) {
        this(resourceName, "ID", id);
    }
}