package com.zinoviev.lms_system.service;

import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.StudentRepository;
import com.zinoviev.lms_system.dto.student.StudentCreateDto;
import com.zinoviev.lms_system.dto.student.StudentUpgradeDto;
import com.zinoviev.lms_system.dto.student.StudentWithGroupDto;
import com.zinoviev.lms_system.exception.ResourceNotFoundException;
import com.zinoviev.lms_system.mapper.StudentMapper;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final GroupRepository groupRepository;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public StudentWithGroupDto createStudent(StudentCreateDto dto) {
        Student newStudent = studentMapper.toEntity(dto);

        // Студента можно создавать без группы
        if (dto.groupId() == null) {
            studentRepository.save(newStudent);

        }
        else {
            Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));
            newStudent.setGroup(group);
            studentRepository.save(newStudent);
        }

        log.info("Студент был добавлен, id: {}", newStudent.getId());

        return studentMapper.toResponseWithGroup(newStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentWithGroupDto getStudent(UUID id) {
        Student foundStudent = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Студент не найден"));
        return studentMapper.toResponseWithGroup(foundStudent);
    }

    @Override
    @Transactional
    public StudentWithGroupDto upgradeStudent(UUID id, StudentUpgradeDto dto) {
        Student oldStudent = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Студент не найден"));
        Student newStudent = studentMapper.upgradeEntity(oldStudent, dto);

        // Обновляем или оставляем ту же группу, если нам пришло значение в поле groupId
        if (dto.groupId() != null) {
            newStudent.setGroup(groupRepository.findById(dto.groupId()).orElseThrow(() -> new ResourceNotFoundException("Группа не найдена")));
        }
        // Если приходит null, то убираем группу
        else {
            newStudent.setGroup(null);
        }
        studentRepository.save(newStudent);

        log.info("Студент был обновлен, id: {}", newStudent.getId());

        return studentMapper.toResponseWithGroup(newStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        studentRepository.deleteById(id);

        log.info("Студент был удален, id: {}", id);
    }

}
