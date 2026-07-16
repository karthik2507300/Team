package com.certifypro.dto.response;

import com.certifypro.model.TestCentre;

public record TestCentreResponse(
        Long centreId,
        String centreName,
        String city,
        String address,
        Integer capacity,
        String contactPerson,
        String status
) {
    public static TestCentreResponse from(TestCentre c) {
        return new TestCentreResponse(
                c.getCentreId(), c.getCentreName(), c.getCity(), c.getAddress(),
                c.getCapacity(), c.getContactPerson(),
                c.getStatus() == null ? null : c.getStatus().name());
    }
}
