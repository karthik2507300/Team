package com.certifypro.security;

/**
 * The authenticated principal placed in the SecurityContext by JwtFilter.
 * Carries the identity needed for ownership checks without a DB hit.
 */
public record AuthPrincipal(Long userId, String email, String role) {
}
