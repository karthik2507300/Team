package com.certifypro.exam.service.impl;

import com.certifypro.exam.common.TestCentreStatus;
import com.certifypro.exam.dto.request.CreateTestCentreRequest;
import com.certifypro.exam.dto.request.UpdateTestCentreRequest;
import com.certifypro.exam.dto.response.PageResponse;
import com.certifypro.exam.dto.response.TestCentreResponse;
import com.certifypro.exam.entity.TestCentre;
import com.certifypro.exam.exception.NotFoundException;
import com.certifypro.exam.repository.TestCentreRepository;
import com.certifypro.exam.service.TestCentreService;
import com.certifypro.exam.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestCentreServiceImpl implements TestCentreService {

    private final TestCentreRepository testCentreRepository;

    public TestCentreServiceImpl(TestCentreRepository testCentreRepository) {
        this.testCentreRepository = testCentreRepository;
    }

    @Override
    @Transactional
    public TestCentreResponse create(CreateTestCentreRequest req) {
        TestCentre c = TestCentre.builder()
                .centreName(req.centreName())
                .city(req.city())
                .address(req.address())
                .capacity(req.capacity() == null ? 0 : req.capacity())
                .contactPerson(req.contactPerson())
                .status(TestCentreStatus.Active)
                .build();
        return TestCentreResponse.from(testCentreRepository.save(c));
    }

    @Override
    public PageResponse<TestCentreResponse> list(int page, int limit) {
        return PageResponse.from(testCentreRepository.findAll(PageUtil.of(page, limit))
                .map(TestCentreResponse::from));
    }

    @Override
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
