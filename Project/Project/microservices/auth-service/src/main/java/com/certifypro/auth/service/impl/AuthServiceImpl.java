package com.certifypro.auth.service.impl;

import com.certifypro.auth.client.CandidateProfileGateway;
import com.certifypro.auth.client.dto.CreateCandidateProfileRequest;
import com.certifypro.auth.common.Role;
import com.certifypro.auth.common.UserStatus;
import com.certifypro.auth.dto.request.LoginRequest;
import com.certifypro.auth.dto.request.RefreshTokenRequest;
import com.certifypro.auth.dto.request.RegisterRequest;
import com.certifypro.auth.dto.response.AuthResponse;
import com.certifypro.auth.entity.User;
import com.certifypro.auth.exception.BusinessException;
import com.certifypro.auth.exception.NotFoundException;
import com.certifypro.auth.repository.UserRepository;
import com.certifypro.auth.security.JwtUtil;
import com.certifypro.auth.security.TokenBlacklist;
import com.certifypro.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CandidateProfileGateway candidateProfileGateway;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    public AuthServiceImpl(UserRepository userRepository,
                           CandidateProfileGateway candidateProfileGateway,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           TokenBlacklist tokenBlacklist) {
        this.userRepository = userRepository;
        this.candidateProfileGateway = candidateProfileGateway;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("An account with this email already exists");
        }

        User user = userRepository.save(User.builder()
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(Role.Candidate)
                .status(UserStatus.Active)
                .build());

        // Create the paired candidate profile in candidate-service (circuit-breaker guarded).
        candidateProfileGateway.createProfile(new CreateCandidateProfileRequest(
                user.getUserId(), req.name(), req.email(), req.phone(),
                req.dateOfBirth(), req.gender(), req.highestQualification(),
                req.professionalExperience(), req.employerName()));

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (user.getStatus() != UserStatus.Active) {
            throw new BusinessException("Account is " + user.getStatus()
                    + ". Please contact the administrator.");
        }
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest req) {
        if (!jwtUtil.isRefreshTokenValid(req.refreshToken())) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
        Claims claims = jwtUtil.parseRefreshToken(req.refreshToken());
        Long userId = claims.get("uid", Number.class).longValue();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("User", userId));

        String access = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());
        return AuthResponse.of(access, req.refreshToken(),
                user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public void logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            tokenBlacklist.add(bearerToken.substring(7));
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        String access = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());
        String refresh = jwtUtil.generateRefreshToken(user.getUserId(), user.getEmail());
        return AuthResponse.of(access, refresh,
                user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
