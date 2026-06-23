package com.zinoviev.lms_system.dto.course;

import com.zinoviev.lms_system.dto.group.GroupSummaryDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CourseWithGroupsDto(

        @NotNull
        UUID id,

        @NotBlank
        String name,

        List<GroupSummaryDto> groupList

) {}
