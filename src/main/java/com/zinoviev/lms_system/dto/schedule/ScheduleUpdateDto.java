package com.zinoviev.lms_system.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record ScheduleUpdateDto (

        @NotNull
        ZonedDateTime startTime

) {
}
