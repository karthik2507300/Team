package com.certifypro.auth.service.impl;

import com.certifypro.auth.common.Role;
import com.certifypro.auth.common.UserStatus;
import com.certifypro.auth.dto.request.CreateStaffRequest;
import com.certifypro.auth.dto.request.UpdateUserRequest;
import com.certifypro.auth.dto.request.UpdateUserStatusRequest;
import com.certifypro.auth.dto.response.PageResponse;
import com.certifypro.auth.dto.response.UserResponse;
import com.certifypro.auth.entity.User;
import com.certifypro.auth.exception.BusinessException;
import com.certifypro.auth.exception.NotFoundException;
import com.certifypro.auth.repository.UserRepository;
import com.certifypro.auth.service.UserService;
import com.certifypro.auth.util.AuditLogUtil;
import com.certifypro.auth.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final String MODULE = "User";
    private static final String DEFAULT_STAFF_PASSWORD = "Password@123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogUtil auditLog;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuditLogUtil auditLog) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLog = auditLog;
    }

    @Override
    public UserResponse getById(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Override
    public java.util.List<UserResponse> findByRole(String role) {
        return userRepository.findAllByRole(parseRole(role)).stream()
                .map(UserResponse::from).toList();
    }

    @Override
    public PageResponse<UserResponse> list(String role, String status, int page, int limit) {
        Role roleFilter = role == null ? null : parseRole(role);
        UserStatus statusFilter = status == null ? null : parseStatus(status);

        Page<User> result;
        if (roleFilter != null && statusFilter != null) {
            result = userRepository.findByRoleAndStatus(roleFilter, statusFilter, PageUtil.of(page, limit));
        } else if (roleFilter != null) {
            result = userRepository.findByRole(roleFilter, PageUtil.of(page, limit));
        } else if (statusFilter != null) {
            result = userRepository.findByStatus(statusFilter, PageUtil.of(page, limit));
        } else {
            result = userRepository.findAll(PageUtil.of(page, limit));
        }
        return PageResponse.from(result.map(UserResponse::from));
    }

    @Override
    @Transactional
    public UserResponse createStaff(CreateStaffRequest req) {
        Role role = parseRole(req.role());
        if (role == Role.Candidate) {
            throw new BusinessException("Candidates self-register; only staff roles can be created here");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("An account with this email already exists");
        }

        String rawPassword = (req.password() == null || req.password().isBlank())
                ? DEFAULT_STAFF_PASSWORD : req.password();
        User user = userRepository.save(User.builder()
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .role(role)
                .status(UserStatus.Active)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build());

        auditLog.log("CREATE", MODULE, user.getUserId());
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = findUser(id);
        if (req.name() != null && !req.name().isBlank()) {
            user.setName(req.name());
        }
        if (req.phone() != null && !req.phone().isBlank()) {
            user.setPhone(req.phone());
        }
        user = userRepository.save(user);
        auditLog.log("UPDATE", MODULE, user.getUserId());
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest req) {
        User user = findUser(id);
        user.setStatus(parseStatus(req.status()));
        user = userRepository.save(user);
        auditLog.log("STATUS_CHANGE", MODULE, user.getUserId());
        return UserResponse.from(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> NotFoundException.of("User", id));
    }

    private Role parseRole(String value) {
        try {
            return Role.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + value);
        }
    }

    private UserStatus parseStatus(String value) {
        try {
            return UserStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + value
                    + " (allowed: Active, Inactive, Suspended)");
        }
    }
}
