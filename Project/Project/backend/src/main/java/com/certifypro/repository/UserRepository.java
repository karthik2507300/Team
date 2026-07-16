package com.certifypro.repository;

import com.certifypro.model.User;
import com.certifypro.model.enums.Role;
import com.certifypro.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);

    Page<User> findByRole(Role role, Pageable p);

    Page<User> findByStatus(UserStatus status, Pageable p);

    Page<User> findByRoleAndStatus(Role role, UserStatus status, Pageable p);
}
