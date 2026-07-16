package com.certifypro.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Consistent API envelope: { success, message, data, errors }.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, List.of());
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data, List.of());
    }

    public static <T> ApiResponse<T> fail(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors == null ? List.of() : errors);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null, List.of());
    }
}
