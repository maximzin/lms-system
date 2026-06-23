package com.zinoviev.lms_system.dto.exception;

import java.time.Instant;
import java.util.Map;

public record ExceptionDto (

    int status,

    String error,

    String message,

    Instant timestamp,

    Map<String, String> fieldErrors

) {
    public ExceptionDto(int status, String error, String message) {
        this(status, error, message, Instant.now(), null);
    }
}
