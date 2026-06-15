package com.zinoviev.lms_system.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CourseCreateDto(

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        UUID teacherId

) {
}
