package com.certifypro.certificate.dto.request;

import jakarta.validation.constraints.NotBlank;

/** status must be one of: Valid, Expired, Revoked, Suspended */
public record UpdateCertificateStatusRequest(
        @NotBlank String status
) {
}
