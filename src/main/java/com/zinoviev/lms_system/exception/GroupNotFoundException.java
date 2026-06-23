package com.zinoviev.lms_system.exception;

public class GroupNotFoundException extends ResourceNotFoundException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
