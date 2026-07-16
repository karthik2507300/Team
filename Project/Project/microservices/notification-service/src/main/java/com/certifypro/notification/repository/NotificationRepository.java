package com.certifypro.notification.repository;

import com.certifypro.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Only paging + CRUD + a derived finder are needed, so we compose
 * PagingAndSortingRepository + CrudRepository (no JpaRepository).
 */
public interface NotificationRepository
        extends PagingAndSortingRepository<Notification, Long>, CrudRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable p);
}
