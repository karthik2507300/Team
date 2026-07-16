package com.certifypro.auth.controller;

import com.certifypro.auth.dto.response.UserResponse;
import com.certifypro.auth.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Service-to-service endpoints (called via Feign, not by the browser).
 * Returns raw DTOs (no ApiResponse envelope) for simple Feign decoding.
 * Permitted without a role in SecurityConfig; only reachable internally or
 * with a valid token through the gateway.
 */
@RestController
@RequestMapping("/api/users/internal")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/by-role/{role}")
    public List<UserResponse> byRole(@PathVariable String role) {
        return userService.findByRole(role);
    }
}
