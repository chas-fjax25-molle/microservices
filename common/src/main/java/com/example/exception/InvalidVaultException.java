package com.example.exception;

public class InvalidVaultException extends RuntimeException {
    public InvalidVaultException(String message) {
        super(message);
    }

    public InvalidVaultException(String message, Throwable cause) {
        super(message, cause);
    }
}
