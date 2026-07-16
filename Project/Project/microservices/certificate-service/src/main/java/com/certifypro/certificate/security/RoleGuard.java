package com.certifypro.certificate.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Ownership/role helper referenced from @PreAuthorize SpEL, e.g.
 *   @PreAuthorize("@roleGuard.isSelfOrAdmin(authentication, #id)")
 */
@Component("roleGuard")
public class RoleGuard {

    public boolean isAdmin(Authentication auth) {
        return hasRole(auth, "Admin");
    }

    public boolean hasRole(Authentication auth, String role) {
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal p)) {
            return false;
        }
        return role.equals(p.role());
    }

    /** True if the caller is acting on their own user record, or is an Admin. */
    public boolean isSelfOrAdmin(Authentication auth, Long userId) {
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal p)) {
            return false;
        }
        return "Admin".equals(p.role()) || (userId != null && userId.equals(p.userId()));
    }
}
