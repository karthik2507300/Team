package com.certifypro.question.config;

import com.certifypro.question.security.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Rebuilds the SecurityContext from the trusted identity headers the API gateway
 * injects after it validates the JWT (X-Auth-User-Id / -Email / -Role).
 * This replaces the monolith's JwtFilter — services no longer parse tokens.
 */
@Component
public class RoleBasedHeaderFilter extends OncePerRequestFilter {

    private final String headerUserId;
    private final String headerUserEmail;
    private final String headerRole;

    public RoleBasedHeaderFilter(
            @Value("${certifypro.auth.header.user-id:X-Auth-User-Id}") String headerUserId,
            @Value("${certifypro.auth.header.user-email:X-Auth-User-Email}") String headerUserEmail,
            @Value("${certifypro.auth.header.role:X-Auth-Role}") String headerRole) {
        this.headerUserId = headerUserId;
        this.headerUserEmail = headerUserEmail;
        this.headerRole = headerRole;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(headerUserId);
        String role = request.getHeader(headerRole);

        if (userId != null && role != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = request.getHeader(headerUserEmail);
            AuthPrincipal principal = new AuthPrincipal(Long.valueOf(userId), email, role);
            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            var authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
