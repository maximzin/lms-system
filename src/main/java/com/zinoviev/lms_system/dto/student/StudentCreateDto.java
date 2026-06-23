package com.zinoviev.lms_system.dto.student;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record StudentCreateDto(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        UUID groupId

) {
}
