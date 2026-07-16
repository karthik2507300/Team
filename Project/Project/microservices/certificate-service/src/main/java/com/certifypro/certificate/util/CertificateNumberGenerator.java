package com.certifypro.certificate.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.UUID;

/** Generates unique certificate numbers, e.g. CERT-2026-AB12CD34. */
@Component
public class CertificateNumberGenerator {

    public String generate() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "CERT-" + Year.now().getValue() + "-" + suffix;
    }
}
