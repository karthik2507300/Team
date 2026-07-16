package com.certifypro.notification.controller;

import com.certifypro.notification.dto.request.InternalNotifyRoleRequest;
import com.certifypro.notification.dto.request.InternalNotifyUserRequest;
import com.certifypro.notification.dto.response.NotificationResponse;
import com.certifypro.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service-to-service endpoints (called via Feign, not by the browser).
 * Returns raw DTOs (no ApiResponse envelope) for simple Feign decoding.
 * Permitted without a role in SecurityConfig (path under /internal).
 */
@RestController
@RequestMapping("/api/notifications/internal")
public class InternalNotificationController {

    private final NotificationService notificationService;

    public InternalNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Create a single Unread notification for one user. */
    @PostMapping
    public ResponseEntity<NotificationResponse> notifyUser(@Valid @RequestBody InternalNotifyUserRequest req) {
        NotificationResponse created = NotificationResponse.from(
                notificationService.notifyUser(req.userId(), req.category(), req.message()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Fan a notification out to every user holding the given role. */
    @PostMapping("/by-role")
    public ResponseEntity<Void> notifyRole(@Valid @RequestBody InternalNotifyRoleRequest req) {
        notificationService.notifyRole(req.role(), req.category(), req.message());
        return ResponseEntity.ok().build();
    }
}
