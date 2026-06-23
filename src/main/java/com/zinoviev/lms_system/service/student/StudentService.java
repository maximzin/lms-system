package com.zinoviev.lms_system.service.student;

import com.zinoviev.lms_system.dto.student.StudentCreateDto;
import com.zinoviev.lms_system.dto.student.StudentUpgradeDto;
import com.zinoviev.lms_system.dto.student.StudentWithGroupDto;

import java.util.UUID;

public interface StudentService {

    StudentWithGroupDto createStudent(StudentCreateDto dto);

    StudentWithGroupDto getStudent(UUID id);

    StudentWithGroupDto upgradeStudent(UUID id, StudentUpgradeDto dto);

    void deleteStudent(UUID id);

}
