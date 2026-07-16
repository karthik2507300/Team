package com.certifypro.service;

import com.certifypro.dto.request.CreateTestCentreRequest;
import com.certifypro.dto.request.UpdateTestCentreRequest;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.TestCentreResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.TestCentre;
import com.certifypro.model.enums.TestCentreStatus;
import com.certifypro.repository.TestCentreRepository;
import com.certifypro.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestCentreService {

    private final TestCentreRepository testCentreRepository;

    public TestCentreService(TestCentreRepository testCentreRepository) {
        this.testCentreRepository = testCentreRepository;
    }

    @Transactional
    public TestCentreResponse create(CreateTestCentreRequest req) {
        TestCentre c = new TestCentre();
        c.setCentreName(req.centreName());
        c.setCity(req.city());
        c.setAddress(req.address());
        c.setCapacity(req.capacity() == null ? 0 : req.capacity());
        c.setContactPerson(req.contactPerson());
        c.setStatus(TestCentreStatus.Active);
        return TestCentreResponse.from(testCentreRepository.save(c));
    }

    public PageResponse<TestCentreResponse> list(int page, int limit) {
        return PageResponse.from(testCentreRepository.findAll(PageUtil.of(page, limit))
                .map(TestCentreResponse::from));
    }

    @Transactional
    public TestCentreResponse update(Long id, UpdateTestCentreRequest req) {
        TestCentre c = testCentreRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("TestCentre", id));
        if (req.centreName() != null) c.setCentreName(req.centreName());
        if (req.city() != null) c.setCity(req.city());
        if (req.address() != null) c.setAddress(req.address());
        if (req.capacity() != null) c.setCapacity(req.capacity());
        if (req.contactPerson() != null) c.setContactPerson(req.contactPerson());
        if (req.status() != null) c.setStatus(parseStatus(req.status()));
        return TestCentreResponse.from(testCentreRepository.save(c));
    }

    private TestCentreStatus parseStatus(String value) {
        try {
            return TestCentreStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + value
                    + " (allowed: Active, Inactive, Blacklisted)");
        }
    }
}
