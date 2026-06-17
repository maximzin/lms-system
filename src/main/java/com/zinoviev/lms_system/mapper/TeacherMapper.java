package com.zinoviev.lms_system.mapper;

import com.zinoviev.lms_system.dto.teacher.TeacherCreateDto;
import com.zinoviev.lms_system.dto.teacher.TeacherWithCoursesAndStudentsDto;
import com.zinoviev.lms_system.dto.teacher.TeacherSummaryDto;
import com.zinoviev.lms_system.dto.teacher.TeacherUpgradeDto;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Student;
import com.zinoviev.lms_system.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseList", ignore = true)
    Teacher toEntity(TeacherCreateDto dto);

    TeacherSummaryDto toSummaryResponse(Teacher teacher);

    @Mapping(target = "id", source = "teacher.id")
    @Mapping(target = "firstName", source = "teacher.firstName")
    @Mapping(target = "lastName", source = "teacher.lastName")
    @Mapping(target = "courseList", source = "courseList")
    @Mapping(target = "studentList", source = "studentList")
    TeacherWithCoursesAndStudentsDto toResponseWithCoursesAndStudents(Teacher teacher, List<Course> courseList, List<Student> studentList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseList", ignore = true)
    Teacher upgradeEntity(@MappingTarget Teacher oldTeacher, TeacherUpgradeDto dto);


}
