package com.zinoviev.lms_system.dto.group;

import com.zinoviev.lms_system.dto.student.StudentSummaryDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GroupWithStudentsDto(

        @NotNull
        UUID id,

        @NotBlank
        String name,

        List<StudentSummaryDto> studentList

) {
}
