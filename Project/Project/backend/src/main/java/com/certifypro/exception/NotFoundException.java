package com.certifypro.exception;

/** Thrown when an entity cannot be found. Maps to HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String entity, Object id) {
        return new NotFoundException(entity + " not found with id: " + id);
    }
}
