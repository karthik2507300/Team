package com.certifypro.result.client;

import com.certifypro.result.client.dto.CertificateDto;
import com.certifypro.result.client.dto.IssueCertificateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to certificate-service. Auto-issues a certificate when a Pass
 * result is published. Guarded by the "certificate-service" circuit breaker via
 * CertificateServiceGateway.
 */
@FeignClient(name = "certificate-service", contextId = "certificateClient", path = "/api/certificates")
public interface CertificateClient {

    @PostMapping("/internal/issue")
    CertificateDto issue(@RequestBody IssueCertificateRequest request);
}
