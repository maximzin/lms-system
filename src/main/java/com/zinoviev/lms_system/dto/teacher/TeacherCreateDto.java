package com.zinoviev.lms_system.dto.teacher;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record TeacherCreateDto(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {
}
