package com.certifypro.result.client;

import com.certifypro.result.client.dto.NotifyRoleRequest;
import com.certifypro.result.client.dto.NotifyUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to notification-service. Notifies evaluators / candidates and fans
 * moderation alerts out to ExamControllers. Guarded by the "notification-service"
 * circuit breaker via NotificationServiceGateway.
 */
@FeignClient(name = "notification-service", contextId = "notificationClient", path = "/api/notifications")
public interface NotificationClient {

    @PostMapping("/internal")
    void notifyUser(@RequestBody NotifyUserRequest request);

    @PostMapping("/internal/by-role")
    void notifyRole(@RequestBody NotifyRoleRequest request);
}
