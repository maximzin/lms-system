package com.zinoviev.lms_system.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ScheduleSummaryDto (

        @NotNull
        UUID id,

        @NotNull
        UUID groupId,

        @NotNull
        UUID teacherId,

        @NotNull
        UUID courseId,

        @NotBlank
        ZonedDateTime startTime,

        @NotBlank
        ZonedDateTime endTime

) {
}
