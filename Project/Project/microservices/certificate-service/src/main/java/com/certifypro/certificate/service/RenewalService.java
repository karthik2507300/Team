package com.certifypro.certificate.service;

import com.certifypro.certificate.dto.request.CreateRenewalRequest;
import com.certifypro.certificate.dto.request.ReviewRenewalRequest;
import com.certifypro.certificate.dto.response.RenewalResponse;

public interface RenewalService {

    RenewalResponse submit(CreateRenewalRequest req);

    RenewalResponse getById(Long id);

    RenewalResponse review(Long id, ReviewRenewalRequest req);
}
