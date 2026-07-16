package com.certifypro.certificate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Certificate microservice (module 2.6).
 * Owns Certificates + RenewalApplications: issues certificates (manually or on
 * result publish), tracks validity/expiry, and processes CPD renewal applications.
 * Consumes candidate-service (candidate userId, program validity) and
 * notification-service (notify candidate on issue) via Feign + circuit breakers.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CertificateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CertificateServiceApplication.class, args);
    }
}
