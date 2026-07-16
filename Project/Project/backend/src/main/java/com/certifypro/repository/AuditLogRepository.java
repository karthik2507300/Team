package com.certifypro.repository;

import com.certifypro.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByUserId(Long userId, Pageable p);

    Page<AuditLog> findByModule(String module, Pageable p);

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
