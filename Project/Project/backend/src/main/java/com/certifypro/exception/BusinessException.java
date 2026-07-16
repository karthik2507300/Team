package com.certifypro.exception;

/** Thrown when a business rule is violated (e.g. capacity exceeded). Maps to HTTP 409. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
