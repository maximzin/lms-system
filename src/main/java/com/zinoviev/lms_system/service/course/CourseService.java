package com.zinoviev.lms_system.service.course;


import com.zinoviev.lms_system.dto.course.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {

    CourseWithTeacherDto createCourse(CourseCreateDto dto);

    CourseWithTeacherDto getCourse(UUID id);

    Page<CourseWithTeacherDto> getAllCourses(Pageable pageable);

    CourseWithTeacherDto upgradeCourse(UUID id, CourseUpgradeDto dto);

    void deleteCourse(UUID id);

    CourseWithGroupsDto addGroupToCourse(UUID courseId, AddGroupToCourseDto dto);

    void deleteGroupFromCourse(UUID courseId, UUID groupId);
    
}
