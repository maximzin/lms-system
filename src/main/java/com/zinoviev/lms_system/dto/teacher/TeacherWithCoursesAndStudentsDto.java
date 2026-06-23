package com.zinoviev.lms_system.dto.teacher;

import com.zinoviev.lms_system.dto.course.CourseSummaryDto;
import com.zinoviev.lms_system.model.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record TeacherWithCoursesAndStudentsDto(

        @NotNull
        UUID id,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        List<CourseSummaryDto> courseList,

        List<Student> studentList

) {
}
