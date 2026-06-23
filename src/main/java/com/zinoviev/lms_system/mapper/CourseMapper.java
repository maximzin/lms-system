package com.zinoviev.lms_system.mapper;

import com.zinoviev.lms_system.dto.course.CourseCreateDto;
import com.zinoviev.lms_system.dto.course.CourseUpgradeDto;
import com.zinoviev.lms_system.dto.course.CourseWithGroupsDto;
import com.zinoviev.lms_system.dto.course.CourseWithTeacherDto;
import com.zinoviev.lms_system.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "groupList", ignore = true)
    Course toEntity(CourseCreateDto dto);

    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherFirstName", source = "teacher.firstName")
    @Mapping(target = "teacherLastName", source = "teacher.lastName")
    CourseWithTeacherDto toResponseWithTeacher(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "groupList", ignore = true)
    Course upgradeEntity(@MappingTarget Course oldCourse, CourseUpgradeDto dto);

    @Mapping(target = "groupList", source = "groupList")
    CourseWithGroupsDto toResponseWithGroups(Course course);
}
