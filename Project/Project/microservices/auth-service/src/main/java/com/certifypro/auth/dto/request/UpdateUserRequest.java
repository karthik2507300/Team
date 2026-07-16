package com.certifypro.auth.dto.request;

/** Editable user fields (Admin). Null fields are left unchanged. */
public record UpdateUserRequest(
        String name,
        String phone
) {
}
