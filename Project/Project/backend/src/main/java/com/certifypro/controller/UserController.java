package com.certifypro.controller;

import com.certifypro.dto.request.CreateStaffRequest;
import com.certifypro.dto.request.UpdateUserRequest;
import com.certifypro.dto.request.UpdateUserStatusRequest;
import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.UserResponse;
import com.certifypro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Own record for any authenticated user; any record for Admin. */
    @GetMapping("/{id}")
    @PreAuthorize("@roleGuard.isSelfOrAdmin(authentication, #id)")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    /** Admin: list users, filter by role and status, paginated. */
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> list(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(userService.list(role, status, page, limit)));
    }

    /** Admin creates a staff user (not a Candidate). */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(@Valid @RequestBody CreateStaffRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Staff user created", userService.createStaff(req)));
    }

    /** Admin edits a user. */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("User updated", userService.update(id, req)));
    }

    /** Admin activates / suspends a user. */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", userService.updateStatus(id, req)));
    }
}
