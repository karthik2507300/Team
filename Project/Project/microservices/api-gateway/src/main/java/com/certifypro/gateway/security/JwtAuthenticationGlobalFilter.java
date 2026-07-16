package com.certifypro.gateway.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Global gateway filter: validates the JWT on every request (except public paths)
 * and forwards the authenticated identity to downstream services as trusted headers.
 * Incoming X-Auth-* headers from clients are always stripped to prevent spoofing.
 */
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGlobalFilter.class);

    private final JwtValidator jwtValidator;
    private final List<String> publicPaths;
    private final String headerUserId;
    private final String headerUserEmail;
    private final String headerRole;

    public JwtAuthenticationGlobalFilter(
            JwtValidator jwtValidator,
            @Value("${gateway.public-paths:/api/auth/register,/api/auth/login,/api/auth/refresh-token}") List<String> publicPaths,
            @Value("${certifypro.auth.header.user-id:X-Auth-User-Id}") String headerUserId,
            @Value("${certifypro.auth.header.user-email:X-Auth-User-Email}") String headerUserEmail,
            @Value("${certifypro.auth.header.role:X-Auth-Role}") String headerRole) {
        this.jwtValidator = jwtValidator;
        this.publicPaths = publicPaths;
        this.headerUserId = headerUserId;
        this.headerUserEmail = headerUserEmail;
        this.headerRole = headerRole;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Always drop any client-supplied identity headers.
        ServerHttpRequest cleaned = request.mutate()
                .headers(h -> {
                    h.remove(headerUserId);
                    h.remove(headerUserEmail);
                    h.remove(headerRole);
                })
                .build();

        if (isPublic(path)) {
            return chain.filter(exchange.mutate().request(cleaned).build());
        }

        String authHeader = cleaned.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Authentication required");
        }

        Claims claims = jwtValidator.validateAccessToken(authHeader.substring(7));
        if (claims == null) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        String userId = String.valueOf(claims.get("uid", Number.class).longValue());
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        ServerHttpRequest authed = cleaned.mutate()
                .header(headerUserId, userId)
                .header(headerUserEmail, email)
                .header(headerRole, role)
                .build();

        log.debug("Authenticated request to {} as user {} ({})", path, userId, role);
        return chain.filter(exchange.mutate().request(authed).build());
    }

    private boolean isPublic(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"success\":false,\"message\":\"" + message
                + "\",\"data\":null,\"errors\":[\"" + message + "\"]}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // run before routing
    }
}
