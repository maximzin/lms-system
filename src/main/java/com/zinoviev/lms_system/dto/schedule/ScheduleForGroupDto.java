package com.zinoviev.lms_system.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ScheduleForGroupDto (

        @NotNull
        UUID groupId,

        @NotBlank
        String groupName,

        List<ScheduleSummaryDto> scheduleList

) {}
