package com.certifypro.dto.response;

import com.certifypro.model.User;

/** User projection that NEVER exposes passwordHash. */
public record UserResponse(
        Long userId,
        String name,
        String email,
        String phone,
        String role,
        String status
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getUserId(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getRole() == null ? null : u.getRole().name(),
                u.getStatus() == null ? null : u.getStatus().name()
        );
    }
}
