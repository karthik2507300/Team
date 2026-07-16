package com.certifypro.auth.security;

/** Authenticated principal rebuilt from the gateway-provided X-Auth-* headers. */
public record AuthPrincipal(Long userId, String email, String role) {
}
