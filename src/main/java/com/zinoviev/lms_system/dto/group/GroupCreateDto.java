package com.zinoviev.lms_system.dto.group;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateDto(

        @NotBlank
        String name

) {
}
