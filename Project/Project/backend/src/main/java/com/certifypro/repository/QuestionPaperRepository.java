package com.certifypro.repository;

import com.certifypro.model.QuestionPaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, Long> {

    Optional<QuestionPaper> findByPaperCode(String code);
}
