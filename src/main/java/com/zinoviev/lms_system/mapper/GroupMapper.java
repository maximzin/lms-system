package com.zinoviev.lms_system.mapper;

import com.zinoviev.lms_system.dto.group.*;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentList", ignore = true)
    Group toEntity(GroupCreateDto dto);

    GroupSummaryDto toSummaryResponse(Group group);

    @Mapping(target = "id", source = "group.id")
    @Mapping(target = "name", source = "group.name")
    @Mapping(target = "studentList", source = "studentList")
    GroupWithStudentsDto toResponseWithStudents(Group group, List<Student> studentList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentList", ignore = true)
    Group upgradeEntity(@MappingTarget Group oldGroup, GroupUpgradeDto dto);

    @Mapping(target = "courseList", source = "courseList")
    GroupWithCoursesDto toResponseWithCourses(Group group);

}
