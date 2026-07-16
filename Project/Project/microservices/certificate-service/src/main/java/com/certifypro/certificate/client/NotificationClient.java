package com.certifypro.certificate.client;

import com.certifypro.certificate.client.dto.NotifyUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to notification-service. Used to notify the candidate's user
 * when a certificate is issued.
 */
@FeignClient(name = "notification-service", path = "/api/notifications")
public interface NotificationClient {

    @PostMapping("/internal")
    void notifyUser(@RequestBody NotifyUserRequest request);
}
