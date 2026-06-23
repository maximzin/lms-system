package com.zinoviev.lms_system.exception;

public abstract class ApplicationException extends RuntimeException {
    private final String message;
    protected ApplicationException(String message) {
        super(message);
        this.message = message;
    }
}