package dev.ozodn.onlineshop.common.dto;

import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(List<T> content, PageMetadata page) {

    /**
     * Creates a PagedResponse from a Spring Data Page object.
     *
     * @param springPage the raw Page returned by Spring Data JPA
     * @param <T>        the type of elements in the list
     * @return a standardized PagedResponse DTO
     */
    public static <T> PagedResponse<T> from(@NonNull Page<T> springPage) {
        return new PagedResponse<>(
                springPage.getContent(),
                PageMetadata.from(springPage)
        );
    }
}