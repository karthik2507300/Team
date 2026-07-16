package com.certifypro.service;

import com.certifypro.dto.response.NotificationResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Notification;
import com.certifypro.model.User;
import com.certifypro.model.enums.NotificationCategory;
import com.certifypro.model.enums.NotificationStatus;
import com.certifypro.model.enums.Role;
import com.certifypro.repository.NotificationRepository;
import com.certifypro.repository.UserRepository;
import com.certifypro.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * In-app notifications. Other modules trigger notifications via notifyUser / notifyRole.
 * The user-facing endpoints (list/read/create) are wired in the NotificationController.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification notifyUser(Long userId, String category, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setCategory(parseCategory(category));
        n.setMessage(message);
        n.setStatus(NotificationStatus.Unread);
        n.setCreatedDate(LocalDateTime.now());
        return notificationRepository.save(n);
    }

    /** Fan-out a notification to every user holding the given role. */
    @Transactional
    public void notifyRole(String role, String category, String message) {
        Role r;
        try {
            r = Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return;
        }
        for (User u : userRepository.findAllByRole(r)) {
            notifyUser(u.getUserId(), category, message);
        }
    }

    public PageResponse<NotificationResponse> listForUser(Long userId, int page, int limit) {
        return PageResponse.from(
                notificationRepository.findByUserId(userId, PageUtil.of(page, limit))
                        .map(NotificationResponse::from));
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Notification", id));
        n.setStatus(NotificationStatus.Read);
        return NotificationResponse.from(notificationRepository.save(n));
    }

    private NotificationCategory parseCategory(String value) {
        try {
            return NotificationCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + value
                    + " (Registration, Exam, Result, Certificate, Renewal)");
        }
    }
}
