package com.certifypro.exam.client.dto;

/** Payload sent to notification-service to raise an in-app notification. */
public record NotificationRequest(
        Long userId,
        String message,
        String category
) {
}
