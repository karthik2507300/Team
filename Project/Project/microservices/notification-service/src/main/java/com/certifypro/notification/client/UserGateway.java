package com.certifypro.notification.client;

import com.certifypro.notification.client.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resilience4j-guarded wrapper around {@link UserClient}.
 * If auth-service is unavailable, role fan-out degrades gracefully: the call
 * returns an empty list (no users notified) instead of failing the request.
 */
@Component
public class UserGateway {

    private static final Logger log = LoggerFactory.getLogger(UserGateway.class);

    private final UserClient userClient;

    public UserGateway(UserClient userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(name = "auth-service", fallbackMethod = "usersByRoleFallback")
    public List<UserDto> usersByRole(String role) {
        return userClient.usersByRole(role);
    }

    @SuppressWarnings("unused")
    private List<UserDto> usersByRoleFallback(String role, Throwable t) {
        log.warn("auth-service unavailable resolving users for role={}: {}. "
                + "No users will be notified for this fan-out.", role, t.getMessage());
        return List.of();
    }
}
