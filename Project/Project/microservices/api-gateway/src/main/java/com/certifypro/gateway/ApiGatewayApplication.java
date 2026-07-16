package com.certifypro.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway — single public entry point (port 8080).
 * Routes every {@code /api/**} request to the right microservice via Eureka
 * ({@code lb://...}) and validates the JWT in {@link JwtAuthenticationGlobalFilter}.
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
