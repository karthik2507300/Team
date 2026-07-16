package com.certifypro.notification.service;

import com.certifypro.notification.dto.response.NotificationResponse;
import com.certifypro.notification.dto.response.PageResponse;
import com.certifypro.notification.entity.Notification;

/**
 * In-app notifications. Other modules trigger notifications via notifyUser / notifyRole.
 * The user-facing endpoints (list/read/create) are wired in the NotificationController.
 */
public interface NotificationService {

    Notification notifyUser(Long userId, String category, String message);

    /** Fan-out a notification to every user holding the given role. */
    void notifyRole(String role, String category, String message);

    PageResponse<NotificationResponse> listForUser(Long userId, int page, int limit);

    NotificationResponse markRead(Long id);
}
