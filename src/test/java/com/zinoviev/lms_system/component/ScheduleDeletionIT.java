package com.zinoviev.lms_system.component;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.ScheduleRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Schedule;
import com.zinoviev.lms_system.model.Teacher;
import com.zinoviev.lms_system.service.schedule.ScheduleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleDeletionIT extends AbstractIT {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScheduleService scheduleService;

    private Group group;
    private Course course;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // Создаём необходимые сущности
        teacher = new Teacher();
        teacher.setFirstName("Olga");
        teacher.setLastName("Zinovieva");
        teacherRepository.save(teacher);

        group = new Group();
        group.setName("group_01");
        groupRepository.save(group);

        course = new Course();
        course.setName("course_01");
        course.setDescription("desc_01");
        course.setTeacher(teacher);
        courseRepository.save(course);
    }

    @AfterEach
    void tearDown() {
        scheduleRepository.deleteAll();
        courseRepository.deleteAll();
        groupRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("Удалить устаревшее расписание и не удалять свежее")
    void deleteOldSchedule_shouldDeleteOldButNotDeleteFreshSchedule() {
        // given
        // Создаём старое расписание (370 дней назад)
        Schedule oldSchedule = new Schedule();
        oldSchedule.setGroup(group);
        oldSchedule.setCourse(course);
        oldSchedule.setTeacher(teacher);
        oldSchedule.setStartTime(ZonedDateTime.now().minusDays(370));
        oldSchedule.setEndTime(ZonedDateTime.now().minusDays(370).plusHours(2));
        scheduleRepository.save(oldSchedule);

        // И свежее расписание (вчера)
        Schedule freshSchedule = new Schedule();
        freshSchedule.setGroup(group);
        freshSchedule.setCourse(course);
        freshSchedule.setTeacher(teacher);
        freshSchedule.setStartTime(ZonedDateTime.now().minusDays(1));
        freshSchedule.setEndTime(ZonedDateTime.now().minusDays(1).plusHours(2));
        scheduleRepository.save(freshSchedule);

        // when
        scheduleService.deleteOldSchedule(365);

        // then
        List<Schedule> remaining = scheduleRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.getFirst().getId()).isEqualTo(freshSchedule.getId());
    }
}
