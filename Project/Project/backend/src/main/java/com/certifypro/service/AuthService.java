package com.certifypro.service;

import com.certifypro.dto.request.LoginRequest;
import com.certifypro.dto.request.RefreshTokenRequest;
import com.certifypro.dto.request.RegisterRequest;
import com.certifypro.dto.response.AuthResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Candidate;
import com.certifypro.model.User;
import com.certifypro.model.enums.CandidateStatus;
import com.certifypro.model.enums.Role;
import com.certifypro.model.enums.UserStatus;
import com.certifypro.repository.CandidateRepository;
import com.certifypro.repository.UserRepository;
import com.certifypro.security.JwtUtil;
import com.certifypro.security.TokenBlacklist;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    public AuthService(UserRepository userRepository,
                       CandidateRepository candidateRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       TokenBlacklist tokenBlacklist) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    /** Candidate self-registration: creates a User (role Candidate) and a Candidate profile. */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("An account with this email already exists");
        }

        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(Role.Candidate);
        user.setStatus(UserStatus.Active);
        user = userRepository.save(user);

        Candidate candidate = new Candidate();
        candidate.setUserId(user.getUserId());
        candidate.setName(req.name());
        candidate.setEmail(req.email());
        candidate.setPhone(req.phone());
        candidate.setDateOfBirth(req.dateOfBirth());
        candidate.setGender(req.gender());
        candidate.setHighestQualification(req.highestQualification());
        candidate.setProfessionalExperience(req.professionalExperience());
        candidate.setEmployerName(req.employerName());
        candidate.setStatus(CandidateStatus.Active);
        candidateRepository.save(candidate);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (user.getStatus() != UserStatus.Active) {
            throw new BusinessException("Account is " + user.getStatus() + ". Please contact the administrator.");
        }

        return buildAuthResponse(user);
    }

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
