package com.certifypro.certificate.security;

/** Authenticated principal rebuilt from the gateway-provided X-Auth-* headers. */
public record AuthPrincipal(Long userId, String email, String role) {
}
