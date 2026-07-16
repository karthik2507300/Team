package com.certifypro.exam.client;

import com.certifypro.exam.client.dto.NotificationRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around {@link NotificationClient}.
 * If notification-service is unavailable the seat allocation still succeeds;
 * the notification is simply logged and dropped (fallback = log + continue).
 */
@Component
public class NotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(NotificationGateway.class);

    private final NotificationClient notificationClient;

    public NotificationGateway(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @CircuitBreaker(name = "notification-service", fallbackMethod = "notifyUserFallback")
    public void notifyUser(Long userId, String message, String category) {
        notificationClient.notifyUser(new NotificationRequest(userId, message, category));
    }

    @SuppressWarnings("unused")
    private void notifyUserFallback(Long userId, String message, String category, Throwable t) {
        log.warn("notification-service unavailable notifying userId={} (category={}): {}. "
                + "Notification dropped.", userId, category, t.getMessage());
    }
}
