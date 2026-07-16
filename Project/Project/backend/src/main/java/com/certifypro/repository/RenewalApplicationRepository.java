package com.certifypro.repository;

import com.certifypro.model.RenewalApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RenewalApplicationRepository extends JpaRepository<RenewalApplication, Long> {
}
