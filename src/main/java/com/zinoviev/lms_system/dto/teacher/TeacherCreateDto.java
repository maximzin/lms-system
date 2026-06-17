package com.zinoviev.lms_system.dto.teacher;

import jakarta.validation.constraints.NotBlank;

public record TeacherCreateDto(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {
}
