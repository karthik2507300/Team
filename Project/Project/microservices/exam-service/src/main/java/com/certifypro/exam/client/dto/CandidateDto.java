package com.certifypro.exam.client.dto;

/**
 * Local copy of candidate-service's candidate projection.
 * Only the fields exam-service needs (name for the hall ticket) are consumed;
 * extras are tolerated by Jackson.
 */
public record CandidateDto(
        Long candidateId,
        Long userId,
        String name,
        String email,
        String phone
) {
}
