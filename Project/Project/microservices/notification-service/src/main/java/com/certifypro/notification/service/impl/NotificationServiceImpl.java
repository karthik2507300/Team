package com.certifypro.notification.service.impl;

import com.certifypro.notification.client.UserGateway;
import com.certifypro.notification.client.dto.UserDto;
import com.certifypro.notification.common.NotificationCategory;
import com.certifypro.notification.common.NotificationStatus;
import com.certifypro.notification.dto.response.NotificationResponse;
import com.certifypro.notification.dto.response.PageResponse;
import com.certifypro.notification.entity.Notification;
import com.certifypro.notification.exception.NotFoundException;
import com.certifypro.notification.repository.NotificationRepository;
import com.certifypro.notification.service.NotificationService;
import com.certifypro.notification.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserGateway userGateway;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserGateway userGateway) {
        this.notificationRepository = notificationRepository;
        this.userGateway = userGateway;
    }

    @Override
    @Transactional
    public Notification notifyUser(Long userId, String category, String message) {
        Notification n = Notification.builder()
                .userId(userId)
                .category(parseCategory(category))
                .message(message)
                .status(NotificationStatus.Unread)
                .createdDate(LocalDateTime.now())
                .build();
        return notificationRepository.save(n);
    }

    /**
     * Fan-out a notification to every user holding the given role. Role membership
     * lives in auth-service, resolved via the circuit-breaker-guarded UserGateway.
     */
    @Override
    @Transactional
    public void notifyRole(String role, String category, String message) {
        List<UserDto> users = userGateway.usersByRole(role);
        for (UserDto u : users) {
            notifyUser(u.userId(), category, message);
        }
    }

    @Override
    public PageResponse<NotificationResponse> listForUser(Long userId, int page, int limit) {
        return PageResponse.from(
                notificationRepository.findByUserId(userId, PageUtil.of(page, limit))
                        .map(NotificationResponse::from));
    }

    @Override
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
