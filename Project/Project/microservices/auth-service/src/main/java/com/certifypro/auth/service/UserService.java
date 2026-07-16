package com.certifypro.auth.service;

import com.certifypro.auth.dto.request.CreateStaffRequest;
import com.certifypro.auth.dto.request.UpdateUserRequest;
import com.certifypro.auth.dto.request.UpdateUserStatusRequest;
import com.certifypro.auth.dto.response.PageResponse;
import com.certifypro.auth.dto.response.UserResponse;

import java.util.List;

/** User administration use cases (Admin) plus self-lookup. */
public interface UserService {

    UserResponse getById(Long id);

    /** Cross-service: all users holding the given role (used by notification fan-out). */
    List<UserResponse> findByRole(String role);

    PageResponse<UserResponse> list(String role, String status, int page, int limit);

    UserResponse createStaff(CreateStaffRequest req);

    UserResponse update(Long id, UpdateUserRequest req);

    UserResponse updateStatus(Long id, UpdateUserStatusRequest req);
}
