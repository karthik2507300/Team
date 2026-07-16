package com.certifypro.controller;

import com.certifypro.dto.request.CreateNotificationRequest;
import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.NotificationResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.security.SecurityUtil;
import com.certifypro.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Notifications for a user. Non-admins always see their own (userId is forced to the caller). */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        boolean isAdmin = "Admin".equals(SecurityUtil.currentRole());
        Long effective = (isAdmin && userId != null) ? userId : SecurityUtil.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(notificationService.listForUser(effective, page, limit)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Notification marked read", notificationService.markRead(id)));
    }

    /** Admin sends a system announcement to a user or to a whole role. */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<Object>> create(@Valid @RequestBody CreateNotificationRequest req) {
        if (req.userId() != null) {
            notificationService.notifyUser(req.userId(), req.category(), req.message());
        } else if (req.role() != null) {
            notificationService.notifyRole(req.role(), req.category(), req.message());
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Provide either userId or role"));
        }
        return ResponseEntity.status(201).body(ApiResponse.ok("Notification sent", null));
    }
}
