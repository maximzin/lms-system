package com.zinoviev.lms_system.exception;

public class ScheduleNotFoundException extends ResourceNotFoundException {
    public ScheduleNotFoundException(String message) {
        super(message);
    }
}
