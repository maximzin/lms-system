package com.zinoviev.lms_system.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveGroupFromCourseDto(

        @NotNull
        UUID groupId,

        @NotNull
        UUID courseId

) {
}
