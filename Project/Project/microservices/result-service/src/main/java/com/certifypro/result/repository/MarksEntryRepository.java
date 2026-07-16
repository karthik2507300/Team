package com.certifypro.result.repository;

import com.certifypro.result.common.MarksStatus;
import com.certifypro.result.entity.MarksEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarksEntryRepository extends JpaRepository<MarksEntry, Long> {

    List<MarksEntry> findByScript_ScriptId(Long scriptId);

    long countByScript_ScriptId(Long scriptId);

    Page<MarksEntry> findByStatus(MarksStatus status, Pageable p);
}
