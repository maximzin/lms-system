package com.zinoviev.lms_system.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StudentWithGroupDto(

        @NotNull
        UUID id,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        UUID groupId,

        String groupName

) {
}
