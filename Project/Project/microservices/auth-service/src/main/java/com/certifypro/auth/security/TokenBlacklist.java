package com.certifypro.auth.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory blacklist of access tokens invalidated via logout.
 * Single-instance only; a shared store (Redis) is needed for multi-instance.
 */
@Component
public class TokenBlacklist {

    private final Set<String> blacklisted = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        blacklisted.add(token);
    }

    public boolean contains(String token) {
        return blacklisted.contains(token);
    }
}
