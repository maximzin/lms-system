package com.zinoviev.lms_system.dto.teacher;

import jakarta.validation.constraints.NotBlank;

public record TeacherUpgradeDto(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {
}
