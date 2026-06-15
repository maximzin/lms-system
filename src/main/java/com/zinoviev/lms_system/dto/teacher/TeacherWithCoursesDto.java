package com.zinoviev.lms_system.dto.teacher;

import com.zinoviev.lms_system.dto.course.CourseSummaryDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record TeacherWithCoursesDto(

        @NotNull
        UUID id,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        List<CourseSummaryDto> courseList

) {
}
