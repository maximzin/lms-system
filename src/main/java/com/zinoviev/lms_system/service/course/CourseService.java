package com.zinoviev.lms_system.service.course;


import com.zinoviev.lms_system.dto.course.*;

import java.util.UUID;

public interface CourseService {

    CourseWithTeacherDto createCourse(CourseCreateDto dto);

    CourseWithTeacherDto getCourse(UUID id);

    CourseWithTeacherDto upgradeCourse(UUID id, CourseUpgradeDto dto);

    void deleteCourse(UUID id);

    CourseWithGroupsDto addGroupToCourse(AddGroupToCourseDto dto);

    void deleteGroupFromCourse(RemoveGroupFromCourseDto dto);
    
}
