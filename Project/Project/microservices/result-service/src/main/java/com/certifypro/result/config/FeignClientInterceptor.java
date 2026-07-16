package com.certifypro.result.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Propagates the authenticated identity (X-Auth-* headers) onto outbound Feign
 * calls so the downstream service's RoleBasedHeaderFilter can authorize them.
 */
@Configuration
public class FeignClientInterceptor {

    private final String headerUserId;
    private final String headerUserEmail;
    private final String headerRole;

    public FeignClientInterceptor(
            @Value("${certifypro.auth.header.user-id:X-Auth-User-Id}") String headerUserId,
            @Value("${certifypro.auth.header.user-email:X-Auth-User-Email}") String headerUserEmail,
            @Value("${certifypro.auth.header.role:X-Auth-Role}") String headerRole) {
        this.headerUserId = headerUserId;
        this.headerUserEmail = headerUserEmail;
        this.headerRole = headerRole;
    }

    @Bean
    public RequestInterceptor authForwardingInterceptor() {
        return (RequestTemplate template) -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                var request = attrs.getRequest();
                forward(template, headerUserId, request.getHeader(headerUserId));
                forward(template, headerUserEmail, request.getHeader(headerUserEmail));
                forward(template, headerRole, request.getHeader(headerRole));
            }
        };
    }

    private void forward(RequestTemplate template, String header, String value) {
        if (value != null && !template.headers().containsKey(header)) {
            template.header(header, value);
        }
    }
}
