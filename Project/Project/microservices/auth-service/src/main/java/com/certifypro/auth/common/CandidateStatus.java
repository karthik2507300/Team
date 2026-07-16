package com.certifypro.auth.common;

/** Mirror of candidate-service's status, used only for the registration Feign call. */
public enum CandidateStatus {
    Active,
    Suspended,
    Debarred
}
