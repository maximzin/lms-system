package com.zinoviev.lms_system.service;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.StudentRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.teacher.TeacherCreateDto;
import com.zinoviev.lms_system.dto.teacher.TeacherWithCoursesAndStudentsDto;
import com.zinoviev.lms_system.dto.teacher.TeacherSummaryDto;
import com.zinoviev.lms_system.dto.teacher.TeacherUpgradeDto;
import com.zinoviev.lms_system.exception.ResourceNotFoundException;
import com.zinoviev.lms_system.mapper.TeacherMapper;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Student;
import com.zinoviev.lms_system.model.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository, TeacherMapper teacherMapper, CourseRepository courseRepository, StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public TeacherSummaryDto createTeacher(TeacherCreateDto dto) {
        Teacher newTeacher = teacherMapper.toEntity(dto);

        teacherRepository.save(newTeacher);

        log.info("Преподаватель был добавлен, id: {}", newTeacher.getId());

        return teacherMapper.toSummaryResponse(newTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherWithCoursesAndStudentsDto getTeacher(UUID id) {
        Teacher foundTeacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден"));

        List<Course> courseList = courseRepository.findAllByTeacherId(id);

        List<Student> studentList = studentRepository.findAllByTeacherId(id);

        return teacherMapper.toResponseWithCoursesAndStudents(foundTeacher, courseList, studentList);
    }

    @Override
    @Transactional
    public TeacherSummaryDto upgradeTeacher(UUID id, TeacherUpgradeDto dto) {
        Teacher oldTeacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден"));
        Teacher newTeacher = teacherMapper.upgradeEntity(oldTeacher, dto);

        teacherRepository.save(newTeacher);

        log.info("Преподаватель был обновлен, id: {}", newTeacher.getId());

        return teacherMapper.toSummaryResponse(newTeacher);
    }

    @Override
    @Transactional
    public void deleteTeacher(UUID id) {
        teacherRepository.deleteById(id);

        log.info("Преподаватель был удален, id: {}", id);
    }

}
