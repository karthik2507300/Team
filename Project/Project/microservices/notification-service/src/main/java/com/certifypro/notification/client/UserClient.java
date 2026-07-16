package com.certifypro.notification.client;

import com.certifypro.notification.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client to auth-service. Resolves the users holding a given role so a
 * role-scoped announcement can be fanned out to each of them.
 */
@FeignClient(name = "auth-service", path = "/api/users")
public interface UserClient {

    @GetMapping("/internal/by-role/{role}")
    List<UserDto> usersByRole(@PathVariable String role);
}
