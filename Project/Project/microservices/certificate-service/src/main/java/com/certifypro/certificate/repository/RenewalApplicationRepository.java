package com.certifypro.certificate.repository;

import com.certifypro.certificate.entity.RenewalApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RenewalApplicationRepository
        extends CrudRepository<RenewalApplication, Long>,
        PagingAndSortingRepository<RenewalApplication, Long> {
}
