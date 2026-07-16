package com.certifypro.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory blacklist of access tokens invalidated via logout.
 * (Sufficient for a single-instance deployment; swap for Redis when scaling out.)
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
