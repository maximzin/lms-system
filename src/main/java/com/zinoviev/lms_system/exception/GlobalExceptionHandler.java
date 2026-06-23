package com.zinoviev.lms_system.exception;

import com.zinoviev.lms_system.dto.exception.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDto> handleNotFound(ResourceNotFoundException ex) {
        ExceptionDto body = new ExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                "Ресурс не найден",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionDto> handleBusiness(BusinessException ex) {
        ExceptionDto body = new ExceptionDto(
                HttpStatus.CONFLICT.value(),
                "Ошибка бизнес-логики",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDto> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage())
        );
        ExceptionDto body = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                "Невалидность полей",
                "Одно или несколько полей невалидны",
                Instant.now(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

}
