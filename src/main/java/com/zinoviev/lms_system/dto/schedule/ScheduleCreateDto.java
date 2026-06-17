package com.zinoviev.lms_system.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ScheduleCreateDto(

        @NotNull
        UUID groupId,

        @NotNull
        UUID courseId,

        @NotNull
        ZonedDateTime startTime

) {
}
