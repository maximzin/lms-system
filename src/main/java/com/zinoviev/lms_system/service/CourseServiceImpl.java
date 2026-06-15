package com.zinoviev.lms_system.service;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.course.*;
import com.zinoviev.lms_system.exception.ResourceNotFoundException;
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

        Teacher teacher = teacherRepository.findById(dto.teacherId()).orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден"));

        newCourse.setTeacher(teacher);

        courseRepository.save(newCourse);

        log.info("Курс был добавлен, id: {}", newCourse.getId());

        CourseWithTeacherDto responseDto = courseMapper.toResponseWithTeacher(newCourse);

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseWithTeacherDto getCourse(UUID id) {
        Course foundCourse = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));

        CourseWithTeacherDto responseDto = courseMapper.toResponseWithTeacher(foundCourse);

        return responseDto;
    }

    @Override
    @Transactional
    public CourseWithTeacherDto upgradeCourse(UUID id, CourseUpgradeDto dto) {
        Course oldCourse = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));
        Course newCourse = courseMapper.upgradeEntity(oldCourse, dto);

        courseRepository.save(newCourse);

        log.info("Курс был обновлен, id: {}", newCourse.getId());

        CourseWithTeacherDto responseDto = courseMapper.toResponseWithTeacher(newCourse);

        return responseDto;
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
        Course course = courseRepository.findById(dto.courseId()).orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));
        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));

        course.getGroupList().add(group);
        group.getCourseList().add(course);
        courseRepository.save(course);

        CourseWithGroupsDto responseDto = courseMapper.toResponseWithGroups(course);
        return responseDto;

    }

    @Override
    @Transactional
    public void deleteGroupFromCourse(RemoveGroupFromCourseDto dto) {
        Course course = courseRepository.findById(dto.courseId()).orElseThrow(() -> new ResourceNotFoundException("Курс не найден"));
        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));

        course.getGroupList().remove(group);
        group.getCourseList().remove(course);
        courseRepository.save(course);

        log.info("Группа была удалена с курса");
    }

}
