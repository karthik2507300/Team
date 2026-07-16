package com.certifypro.question.repository;

import com.certifypro.question.entity.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperQuestionRepository extends JpaRepository<PaperQuestion, Long> {

    List<PaperQuestion> findByPaperIdOrderBySequenceOrderAsc(Long paperId);
}
