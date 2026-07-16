package com.certifypro.notification.client.dto;

/**
 * Local copy of the user shape returned by auth-service's internal endpoints.
 * Only the fields needed for fan-out (userId) are used; the rest mirror the
 * source DTO for tolerant JSON decoding.
 */
public record UserDto(
        Long userId,
        String name,
        String email,
        String phone,
        String role,
        String status
) {
}
