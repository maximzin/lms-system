package com.zinoviev.lms_system.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TeacherSummaryDto(

        @NotNull
        UUID id,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {
}
