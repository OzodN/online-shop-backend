package dev.ozodn.onlineshop.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;

/**
 * Creates RFC 7807 problem detail representations for application errors.
 */
@Component
public class ProblemDetailFactory {

    private static final String ERROR_BASE_URL =
            "https://onlineshop.dev/errors/";

    /**
     * Creates an RFC 7807 problem detail response with the specified error attributes.
     *
     * @param status HTTP status code for the response
     * @param title short summary of the problem type
     * @param detail human-readable explanation of the specific error
     * @param errorType categorized API error type
     * @param request current HTTP request providing the instance URI
     * @return populated {@link ProblemDetail} with timestamp metadata
     */
    public ProblemDetail create(
            HttpStatus status,
            String title,
            String detail,
            ApiErrorType errorType,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );

        problemDetail.setTitle(title);
        problemDetail.setType(
                URI.create(ERROR_BASE_URL + errorType.getPath())
        );
        problemDetail.setInstance(
                URI.create(request.getRequestURI())
        );
        problemDetail.setProperty(
                "timestamp",
                Instant.now()
        );

        return problemDetail;
    }

    /**
     * Builds an RFC 7807 problem detail response from a {@link BaseException}.
     *
     * @param exception domain exception containing error details
     * @param request current HTTP request providing the instance URI
     * @return populated {@link ProblemDetail} with timestamp metadata
     */
    public ProblemDetail fromBaseException(
            BaseException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                exception.getStatus(),
                exception.getMessage()
        );

        problemDetail.setTitle(exception.getTitle());
        problemDetail.setType(exception.getType());
        problemDetail.setInstance(
                URI.create(request.getRequestURI())
        );
        problemDetail.setProperty(
                "timestamp",
                Instant.now()
        );

        return problemDetail;
    }
}