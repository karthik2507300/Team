package com.certifypro.exam.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/** Standard paginated payload returned inside ApiResponse.data for list endpoints. */
public record PageResponse<T>(
        List<T> content,
        int page,
        int limit,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(
                p.getContent(), p.getNumber(), p.getSize(),
                p.getTotalElements(), p.getTotalPages(), p.isLast());
    }
}
