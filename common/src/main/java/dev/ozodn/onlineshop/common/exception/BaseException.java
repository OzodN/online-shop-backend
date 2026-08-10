package dev.ozodn.onlineshop.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final String title;
    private final URI type;

    protected BaseException(String message, HttpStatus status, String title, String typePath) {
        super(message);
        this.status = status;
        this.title = title;
        this.type = URI.create("https://onlineshop.dev/errors/" + typePath);
    }
}
