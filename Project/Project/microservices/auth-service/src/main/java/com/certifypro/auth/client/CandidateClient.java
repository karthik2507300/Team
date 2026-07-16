package com.certifypro.auth.client;

import com.certifypro.auth.client.dto.CreateCandidateProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to candidate-service. Used during registration to create the
 * candidate profile that pairs with the newly created User.
 */
@FeignClient(name = "candidate-service", path = "/api/candidates")
public interface CandidateClient {

    @PostMapping("/internal/register")
    void createProfile(@RequestBody CreateCandidateProfileRequest request);
}
