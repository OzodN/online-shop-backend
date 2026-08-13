package dev.ozodn.onlineshop.common.dto;

import lombok.NonNull;
import org.springframework.data.domain.Page;

public record PageMetadata(
        int number,
        int size,
        long totalElements,
        int totalPages
) {

    public static PageMetadata from(@NonNull Page<?> springPage) {
        return new PageMetadata(
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );
    }
}