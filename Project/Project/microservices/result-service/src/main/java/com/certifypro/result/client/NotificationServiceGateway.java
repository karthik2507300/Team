package com.certifypro.result.client;

import com.certifypro.result.client.dto.NotifyRoleRequest;
import com.certifypro.result.client.dto.NotifyUserRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around notification-service. Notifications are
 * best-effort: if notification-service is unavailable the failure is logged and
 * the calling flow (script assign / moderation / publish) continues. Circuit-breaker
 * instance "notification-service" is configured in config-repo/result-service.yml.
 */
@Component
public class NotificationServiceGateway {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceGateway.class);

    private final NotificationClient notificationClient;

    public NotificationServiceGateway(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    /** Notify a single user; on failure log and continue. */
    @CircuitBreaker(name = "notification-service", fallbackMethod = "notifyUserFallback")
    public void notifyUser(Long userId, String category, String message) {
        notificationClient.notifyUser(new NotifyUserRequest(userId, message, category));
    }

    @SuppressWarnings("unused")
    private void notifyUserFallback(Long userId, String category, String message, Throwable t) {
        log.warn("notification-service unavailable notifying userId={}: {}. Notification dropped.",
                userId, t.getMessage());
    }

    /** Fan a notification out to every user of a role; on failure log and continue. */
    @CircuitBreaker(name = "notification-service", fallbackMethod = "notifyRoleFallback")
    public void notifyRole(String role, String category, String message) {
        notificationClient.notifyRole(new NotifyRoleRequest(role, message, category));
    }

    @SuppressWarnings("unused")
    private void notifyRoleFallback(String role, String category, String message, Throwable t) {
        log.warn("notification-service unavailable notifying role={}: {}. Notification dropped.",
                role, t.getMessage());
    }
}
