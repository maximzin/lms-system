package com.zinoviev.lms_system.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Сокращенная версия для вывода группы
public record StudentSummaryDto(

        @NotNull
        UUID id,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {
}
