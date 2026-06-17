package com.zinoviev.lms_system.dto.course;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddGroupToCourseDto(

        @NotNull
        UUID groupId,

        @NotNull
        UUID courseId

) {
}
