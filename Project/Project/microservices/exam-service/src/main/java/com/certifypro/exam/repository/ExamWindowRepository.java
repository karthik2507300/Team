package com.certifypro.exam.repository;

import com.certifypro.exam.entity.ExamWindow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamWindowRepository extends JpaRepository<ExamWindow, Long> {

    Page<ExamWindow> findByProgramId(Long programId, Pageable p);
}
