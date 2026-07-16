package com.certifypro.exam.dto.request;

/** status may be Active/Inactive/Blacklisted. Null fields left unchanged. */
public record UpdateTestCentreRequest(
        String centreName,
        String city,
        String address,
        Integer capacity,
        String contactPerson,
        String status
) {
}
