package dev.ozodn.onlineshop.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.URI;

/**
 * Serves as the base runtime exception for application errors mapped to RFC 7807 problem details.
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final String title;
    private final URI type;

    /**
     * Constructs a new base exception with HTTP error details and problem type path.
     *
     * @param message detail message describing the error
     * @param status HTTP response status associated with this error
     * @param title short, human-readable summary of the problem type
     * @param typePath relative path appended to the base problem type URI
     */
    protected BaseException(String message, HttpStatus status, String title, String typePath) {
        super(message);
        this.status = status;
        this.title = title;
        this.type = URI.create("https://onlineshop.dev/errors/" + typePath);
    }
}
