package com.certifypro.dto.response;

import com.certifypro.model.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long notificationId,
        Long userId,
        String message,
        String category,
        String status,
        LocalDateTime createdDate
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getNotificationId(), n.getUserId(), n.getMessage(),
                n.getCategory() == null ? null : n.getCategory().name(),
                n.getStatus() == null ? null : n.getStatus().name(),
                n.getCreatedDate());
    }
}
