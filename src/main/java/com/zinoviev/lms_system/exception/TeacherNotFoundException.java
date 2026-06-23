package com.zinoviev.lms_system.exception;

public class TeacherNotFoundException extends ResourceNotFoundException {
    public TeacherNotFoundException(String message) {
        super(message);
    }
}
