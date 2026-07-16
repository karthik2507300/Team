package com.certifypro.auth.service;

import com.certifypro.auth.dto.request.LoginRequest;
import com.certifypro.auth.dto.request.RefreshTokenRequest;
import com.certifypro.auth.dto.request.RegisterRequest;
import com.certifypro.auth.dto.response.AuthResponse;

/** Authentication use cases: registration, login, token refresh, logout. */
public interface AuthService {

    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

    AuthResponse refresh(RefreshTokenRequest req);

    void logout(String bearerToken);
}
