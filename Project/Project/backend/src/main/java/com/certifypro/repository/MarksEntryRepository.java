package com.certifypro.repository;

import com.certifypro.model.MarksEntry;
import com.certifypro.model.enums.MarksStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarksEntryRepository extends JpaRepository<MarksEntry, Long> {

    List<MarksEntry> findByScriptId(Long scriptId);

    long countByScriptId(Long scriptId);

    Page<MarksEntry> findByStatus(MarksStatus status, Pageable p);
}
