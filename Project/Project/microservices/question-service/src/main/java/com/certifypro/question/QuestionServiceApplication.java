package com.certifypro.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Question bank &amp; paper composition microservice (module 2.4).
 * Owns Question, QuestionPaper and PaperQuestion. Consumes no other service:
 * cross-service ids (programId, windowId, createdById) are accepted as plain
 * Longs without validation. Exposes an internal PaperDto for result-service.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class QuestionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuestionServiceApplication.class, args);
    }
}
