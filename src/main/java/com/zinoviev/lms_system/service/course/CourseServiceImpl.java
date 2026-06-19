package com.zinoviev.lms_system.service.course;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.course.*;
import com.zinoviev.lms_system.exception.CourseNotFoundException;
import com.zinoviev.lms_system.exception.GroupNotFoundException;
import com.zinoviev.lms_system.exception.TeacherNotFoundException;
import com.zinoviev.lms_system.mapper.CourseMapper;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;

    public CourseServiceImpl(CourseMapper courseMapper, CourseRepository courseRepository, TeacherRepository teacherRepository, GroupRepository groupRepository) {
        this.courseMapper = courseMapper;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public CourseWithTeacherDto createCourse(CourseCreateDto dto) {
        Course newCourse = courseMapper.toEntity(dto);

        Teacher teacher = teacherRepository.findById(dto.teacherId()).orElseThrow(() -> new TeacherNotFoundException("Преподаватель не найден"));

        newCourse.setTeacher(teacher);

        courseRepository.save(newCourse);

        log.info("Курс был добавлен, id: {}", newCourse.getId());

        return courseMapper.toResponseWithTeacher(newCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseWithTeacherDto getCourse(UUID id) {
        Course foundCourse = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Курс не найден"));

        return courseMapper.toResponseWithTeacher(foundCourse);
    }

    @Override
    @Transactional
    public CourseWithTeacherDto upgradeCourse(UUID id, CourseUpgradeDto dto) {
        Course oldCourse = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
        Course newCourse = courseMapper.upgradeEntity(oldCourse, dto);

        Teacher newTeacher = teacherRepository.findById(dto.teacherId()).orElseThrow(() -> new TeacherNotFoundException("Преподаватель не найден"));
        newCourse.setTeacher(newTeacher);

        courseRepository.save(newCourse);

        log.info("Курс был обновлен, id: {}", newCourse.getId());

        return courseMapper.toResponseWithTeacher(newCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(UUID id) {
        courseRepository.deleteById(id);

        log.info("Курс был удален, id: {}", id);
    }

    @Override
    @Transactional
    public CourseWithGroupsDto addGroupToCourse(AddGroupToCourseDto dto) {
        Course course = courseRepository.findById(dto.courseId()).orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        course.getGroupList().add(group);
        courseRepository.save(course);

        return courseMapper.toResponseWithGroups(course);

    }

    @Override
    @Transactional
    public void deleteGroupFromCourse(RemoveGroupFromCourseDto dto) {
        Course course = courseRepository.findById(dto.courseId()).orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        course.getGroupList().remove(group);
        courseRepository.save(course);

        log.info("Группа была удалена с курса");
    }

}
