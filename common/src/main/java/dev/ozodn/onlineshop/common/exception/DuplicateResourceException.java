package dev.ozodn.onlineshop.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to create or update an entity with conflicting unique attributes.
 */
public class DuplicateResourceException extends BaseException {

    /**
     * Constructs a duplicate resource exception formatted with the conflicting field and value.
     *
     * @param resourceName name of the duplicated entity or resource
     * @param fieldName name of the field causing the uniqueness conflict
     * @param fieldValue value that already exists in the system
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s with %s '%s' already exists", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT,
                "Duplicate " + resourceName,
                "duplicate-" + resourceName.toLowerCase().replace(" ", "-")
        );
    }
}
