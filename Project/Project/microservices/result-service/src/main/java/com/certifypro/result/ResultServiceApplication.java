package com.certifypro.result;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Evaluation &amp; Results microservice (module 2.6).
 * Owns ScriptAllocation, MarksEntry, CandidateResult, ReEvaluationRequest.
 * The most cross-connected service: consumes question-service (paper totals),
 * exam-service (seat/window lookup), candidate-service (grading scale, candidate userId),
 * certificate-service (auto-issue on Pass publish) and notification-service, each wrapped
 * in a Resilience4j circuit-breaker gateway.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ResultServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResultServiceApplication.class, args);
    }
}
