package com.freshbazaar.identity.service.api.exception;

public class DuplicateCredentialException extends RuntimeException {
    public DuplicateCredentialException(String message) {
        super(message);
    }
}
