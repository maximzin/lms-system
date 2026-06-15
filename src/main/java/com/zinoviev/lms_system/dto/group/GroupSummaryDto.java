package com.zinoviev.lms_system.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Сокращенная версия для вывода группы при создании
public record GroupSummaryDto(

        @NotNull
        UUID id,

        @NotBlank
        String name
) {
}
