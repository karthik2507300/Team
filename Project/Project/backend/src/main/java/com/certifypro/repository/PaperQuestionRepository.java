package com.certifypro.repository;

import com.certifypro.model.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperQuestionRepository extends JpaRepository<PaperQuestion, Long> {

    List<PaperQuestion> findByPaperIdOrderBySequenceOrderAsc(Long paperId);
}
