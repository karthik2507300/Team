package com.certifypro.exam.client;

import com.certifypro.exam.client.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to notification-service. Used to notify a candidate when a seat
 * is allocated (replaces the monolith's direct NotificationService call).
 */
@FeignClient(name = "notification-service", contextId = "notificationClient", path = "/api/notifications")
public interface NotificationClient {

    @PostMapping("/internal")
    void notifyUser(@RequestBody NotificationRequest request);
}
