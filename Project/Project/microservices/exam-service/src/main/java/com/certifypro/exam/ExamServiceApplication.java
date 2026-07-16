package com.certifypro.exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Exam scheduling microservice (module 2.3).
 * Owns ExamWindow, TestCentre, SeatAllocation and InvigilatorAssignment.
 * Consumes candidate-service (candidate/program lookup for hall tickets) and
 * notification-service (seat-allocation notifications) via Feign.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ExamServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamServiceApplication.class, args);
    }
}
