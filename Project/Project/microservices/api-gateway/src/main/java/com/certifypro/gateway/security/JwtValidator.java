package com.certifypro.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Validates access tokens using the shared HMAC secret (same secret auth-service
 * signs with). Only signature + expiry + token type are checked here.
 */
@Component
public class JwtValidator {

    private final SecretKey accessKey;

    public JwtValidator(@Value("${certifypro.jwt.secret}") String secret) {
        this.accessKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(accessKey).build()
                .parseSignedClaims(token).getPayload();
    }

    /** Returns the validated claims, or {@code null} if the token is invalid/expired. */
    public Claims validateAccessToken(String token) {
        try {
            Claims claims = parse(token);
            return "access".equals(claims.get("type", String.class)) ? claims : null;
        } catch (Exception e) {
            return null;
        }
    }
}
