package com.certifypro.auth.repository;

import com.certifypro.auth.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * Extends JpaRepository because the audit search needs a custom @Query with
 * multiple optional filters (beyond PagingAndSortingRepository capabilities).
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
            SELECT a FROM AuditLog a
            WHERE (:userId IS NULL OR a.userId = :userId)
              AND (:module IS NULL OR a.module = :module)
              AND (:from   IS NULL OR a.timestamp >= :from)
              AND (:to     IS NULL OR a.timestamp <= :to)
            ORDER BY a.timestamp DESC
            """)
    Page<AuditLog> search(@Param("userId") Long userId,
                          @Param("module") String module,
                          @Param("from") LocalDateTime from,
                          @Param("to") LocalDateTime to,
                          Pageable p);
}
