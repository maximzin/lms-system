package com.zinoviev.lms_system.service.schedule;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.ScheduleRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.schedule.*;
import com.zinoviev.lms_system.exception.*;
import com.zinoviev.lms_system.mapper.ScheduleMapper;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Schedule;
import com.zinoviev.lms_system.model.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new BusinessException("Время начала занятий не должно быть позже времени окончания");
        }

        if (!courseRepository.existsGroupInCourse(dto.courseId(), dto.groupId())) {
            log.error("Группа {} не прикреплена к курсу {}", dto.groupId(), dto.courseId());
            throw new BusinessException("Группа не прикреплена к курсу");
        }

        // Проверка есть ли пересечение у учителя уже имеющихся занятий
        Course course = courseRepository.findById(dto.courseId()).orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
        boolean overlaps = scheduleRepository.existsOverlappingForTeacher(
                course.getTeacher().getId(),
                dto.startTime(),
                dto.endTime());
        if (overlaps) {
            log.error("Добавление записи Schedule: у преподавателя {} уже есть пересечение с уже имеющимся расписанием", course.getTeacher().getId());
            throw new BusinessException("У преподавателя уже есть занятие в это время");
        }

        Schedule newSchedule = scheduleMapper.toEntity(dto);

        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        newSchedule.setGroup(group);
        newSchedule.setCourse(course);
        newSchedule.setTeacher(course.getTeacher());

        scheduleRepository.save(newSchedule);

        log.info("Запись расписания была добавлена, id: {}", newSchedule.getId());

        return scheduleMapper.toSummaryResponse(newSchedule);
    }

    @Override
    @Transactional
    public ScheduleSummaryDto updateSchedule(UUID scheduleId, ScheduleUpdateDto dto) {
        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new BusinessException("Время начала занятий не должно быть позже времени окончания");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleNotFoundException("Запись расписания не найдена"));

        // Проверяем на пересечение, исключая текущую запись
        boolean overlapsExcludingCurrent = scheduleRepository.existsOverlappingForTeacherExcludingId(
                schedule.getCourse().getTeacher().getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                scheduleId);
        if (overlapsExcludingCurrent) {
            log.error("Обновление записи Schedule: у преподавателя {} есть пересечение с уже имеющимся расписанием", schedule.getCourse().getTeacher().getId());
            throw new BusinessException("У преподавателя уже есть занятие в это время");
        }

        schedule.setStartTime(dto.startTime());
        schedule.setEndTime(dto.endTime());

        scheduleRepository.save(schedule);

        return scheduleMapper.toSummaryResponse(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID id) {
        scheduleRepository.deleteById(id);
        log.info("Запись расписания была удалена, id: {}", id);
    }

    @Override
    public ScheduleForGroupDto getScheduleForGroup(UUID groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        List<Schedule> scheduleList = scheduleRepository.findAllByGroupIdOrderByStartTimeAsc(groupId);

        return scheduleMapper.toScheduleForGroup(group, scheduleList);
    }

    @Override
    public ScheduleForTeacherDto getScheduleForTeacher(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new TeacherNotFoundException("Преподаватель не найден"));

        List<Schedule> scheduleList = scheduleRepository.findAllByTeacherIdOrderByStartTimeAsc(teacherId);

        return scheduleMapper.toScheduleForTeacher(teacher, scheduleList);
    }

    // Удаляет расписание, которому больше года
    @Override
    @Transactional
    public void deleteOldSchedule(Integer durationDaysToDelete) {
        LocalDate oldLimitDate = LocalDate.now().minusDays(durationDaysToDelete);
        int deletedCount = scheduleRepository.deleteOldSchedulesByOldLimitDate(oldLimitDate);
        log.info("Удалено {} записей устаревшего расписания, которому более {} дней", deletedCount, durationDaysToDelete);
    }
}
