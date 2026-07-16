package com.certifypro.auth.repository;

import com.certifypro.auth.common.Role;
import com.certifypro.auth.common.UserStatus;
import com.certifypro.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Extends JpaRepository because users are queried by derived finders and by id
 * (login, existence checks) beyond simple paging/sorting.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);

    Page<User> findByRole(Role role, Pageable p);

    Page<User> findByStatus(UserStatus status, Pageable p);

    Page<User> findByRoleAndStatus(Role role, UserStatus status, Pageable p);
}
