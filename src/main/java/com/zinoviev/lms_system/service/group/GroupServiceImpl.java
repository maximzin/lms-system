package com.zinoviev.lms_system.service.group;

import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.StudentRepository;
import com.zinoviev.lms_system.dto.group.*;
import com.zinoviev.lms_system.exception.GroupNotFoundException;
import com.zinoviev.lms_system.exception.ResourceNotFoundException;
import com.zinoviev.lms_system.exception.StudentNotFoundException;
import com.zinoviev.lms_system.mapper.GroupMapper;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupMapper groupMapper;

    public GroupServiceImpl(GroupMapper groupMapper, GroupRepository groupRepository, StudentRepository studentRepository) {
        this.groupMapper = groupMapper;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public GroupSummaryDto createGroup(GroupCreateDto dto) {
        Group newGroup = groupMapper.toEntity(dto);

        groupRepository.save(newGroup);

        log.info("Группа была добавлена, id: {}", newGroup.getId());

        return groupMapper.toSummaryResponse(newGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupWithStudentsDto getGroup(UUID id) {
        Group foundGroup = groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        List<Student> students = studentRepository.findAllByGroupId(id);

        return groupMapper.toResponseWithStudents(foundGroup, students);
    }

    @Override
    @Transactional
    public GroupSummaryDto upgradeGroup(UUID id, GroupUpgradeDto dto) {
        Group oldGroup = groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));
        Group newGroup = groupMapper.upgradeEntity(oldGroup, dto);

        groupRepository.save(newGroup);

        log.info("Группа была обновлена, id: {}", newGroup.getId());

        return groupMapper.toSummaryResponse(newGroup);
    }

    @Override
    @Transactional
    public void deleteGroup(UUID id) {
        groupRepository.deleteById(id);

        log.info("Группа была удалена, id: {}", id);
    }

    @Override
    @Transactional
    public GroupWithStudentsDto uniteStudentsInGroup(UniteStudentsInGroupDto dto) {

        Group group = groupRepository.findById(dto.groupId()).orElseThrow(() -> new GroupNotFoundException("Группа не найдена"));

        dto.studentIdList()
                .forEach(studentId -> {
                    Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Один из студентов не найден, id=" + studentId));
                    student.setGroup(group);
                    studentRepository.save(student);
                });

        List<Student> newStudentList = studentRepository.findAllByGroupId(dto.groupId());

        return groupMapper.toResponseWithStudents(group, newStudentList);

    }

}
