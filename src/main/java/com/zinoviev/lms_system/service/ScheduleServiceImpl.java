package com.zinoviev.lms_system.service;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.ScheduleRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.group.GroupCreateDto;
import com.zinoviev.lms_system.dto.group.GroupSummaryDto;
import com.zinoviev.lms_system.dto.schedule.*;
import com.zinoviev.lms_system.exception.BusinessException;
import com.zinoviev.lms_system.exception.ResourceNotFoundException;
import com.zinoviev.lms_system.mapper.ScheduleMapper;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Schedule;
import com.zinoviev.lms_system.model.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper, GroupRepository groupRepository, CourseRepository courseRepository, TeacherRepository teacherRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    @Transactional
    public ScheduleSummaryDto createSchedule(ScheduleCreateDto dto) {

        if (courseRepository.existsGroupInCourse(dto.courseId(), dto.groupId())) {
            log.error("Группа {} не прикреплена к курсу {}", dto.groupId(), dto.courseId());
            throw new BusinessException("Группа не прикреплена к курсу");
        }

        Schedule newSchedule = scheduleMapper.toEntity(dto);

        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));
        Course course = courseRepository.findById(dto.courseId()).orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));

        newSchedule.setGroup(group);
        newSchedule.setCourse(course);
        newSchedule.setTeacher(course.getTeacher());

        scheduleRepository.save(newSchedule);

        log.info("Запись расписания была добавлена, id: {}", newSchedule.getId());

        ScheduleSummaryDto responseDto = scheduleMapper.toSummaryResponse(newSchedule);

        return responseDto;
    }

    @Override
    @Transactional
    public ScheduleSummaryDto updateSchedule(UUID scheduleId, ScheduleUpdateDto dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ResourceNotFoundException("Запись расписания не найдена"));
        schedule.setStartTime(dto.startTime());

        scheduleRepository.save(schedule);

        ScheduleSummaryDto responseDto = scheduleMapper.toSummaryResponse(schedule);
        return responseDto;
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID id) {
        scheduleRepository.deleteById(id);
        log.info("Запись расписания была удалена, id: {}", id);
    }

    @Override
    public ScheduleForGroupDto getScheduleForGroup(UUID groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));

        List<Schedule> scheduleList = scheduleRepository.findAllByGroupId(groupId)
                .stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        ScheduleForGroupDto responseDto = scheduleMapper.toScheduleForGroup(group, scheduleList);
        return responseDto;
    }

    @Override
    public ScheduleForTeacherDto getScheduleForTeacher(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден"));

        List<Schedule> scheduleList = scheduleRepository.findAllByTeacherId(teacherId)
                .stream()
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .toList();

        ScheduleForTeacherDto responseDto = scheduleMapper.toScheduleForTeacher(teacher, scheduleList);
        return responseDto;
    }
}
