package com.certifypro.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Reporting &amp; analytics microservice (module 2.7).
 * Owns ExaminationReport and produces cross-scope metrics (Program / Window /
 * Centre / Period). Full aggregation reads data owned by exam-, result-,
 * certificate- and candidate-service via Feign; where those services do not yet
 * expose a suitable aggregate endpoint the metric is recorded as 0 (see
 * ReportServiceImpl). All downstream calls are guarded by circuit breakers.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}
