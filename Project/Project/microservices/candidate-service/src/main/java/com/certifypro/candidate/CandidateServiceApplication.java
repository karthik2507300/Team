package com.certifypro.candidate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Candidate-service microservice.
 * Owns Candidate profiles, CertificationProgram, GradingScale and ProgramEnrolment.
 * Consumes no downstream services (self-contained); auth-service calls its
 * internal registration endpoint at candidate creation time.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CandidateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CandidateServiceApplication.class, args);
    }
}
