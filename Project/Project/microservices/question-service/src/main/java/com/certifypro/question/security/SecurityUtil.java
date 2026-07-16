package com.certifypro.question.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Reads the current AuthPrincipal from the security context. */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static AuthPrincipal currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthPrincipal p) {
            return p;
        }
        return null;
    }

    public static Long currentUserId() {
        AuthPrincipal p = currentPrincipal();
        return p == null ? null : p.userId();
    }

    public static String currentRole() {
        AuthPrincipal p = currentPrincipal();
        return p == null ? null : p.role();
    }
}
