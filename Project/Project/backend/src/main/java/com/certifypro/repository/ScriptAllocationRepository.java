package com.certifypro.repository;

import com.certifypro.model.ScriptAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptAllocationRepository extends JpaRepository<ScriptAllocation, Long> {

    Page<ScriptAllocation> findByEvaluatorId(Long evaluatorId, Pageable p);

    List<ScriptAllocation> findByAllocationId(Long allocationId);
}
