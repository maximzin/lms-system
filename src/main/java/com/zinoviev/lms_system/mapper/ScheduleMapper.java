package com.zinoviev.lms_system.mapper;

import com.zinoviev.lms_system.dto.schedule.ScheduleCreateDto;
import com.zinoviev.lms_system.dto.schedule.ScheduleForGroupDto;
import com.zinoviev.lms_system.dto.schedule.ScheduleForTeacherDto;
import com.zinoviev.lms_system.dto.schedule.ScheduleSummaryDto;
import com.zinoviev.lms_system.dto.teacher.TeacherWithCoursesDto;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Schedule;
import com.zinoviev.lms_system.model.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Schedule toEntity(ScheduleCreateDto dto);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "courseId", source = "course.id")
    ScheduleSummaryDto toSummaryResponse(Schedule schedule);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    @Mapping(target = "scheduleList", source = "scheduleList")
    ScheduleForGroupDto toScheduleForGroup(Group group, List<Schedule> scheduleList);

    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherFirstName", source = "teacher.firstName")
    @Mapping(target = "teacherLastName", source = "teacher.lastName")
    @Mapping(target = "scheduleList", source = "scheduleList")
    ScheduleForTeacherDto toScheduleForTeacher(Teacher teacher, List<Schedule> scheduleList);
}
