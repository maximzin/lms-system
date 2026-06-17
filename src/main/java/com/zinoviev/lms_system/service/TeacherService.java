package com.zinoviev.lms_system.service;

import com.zinoviev.lms_system.dto.teacher.TeacherCreateDto;
import com.zinoviev.lms_system.dto.teacher.TeacherWithCoursesAndStudentsDto;
import com.zinoviev.lms_system.dto.teacher.TeacherSummaryDto;
import com.zinoviev.lms_system.dto.teacher.TeacherUpgradeDto;

import java.util.UUID;

public interface TeacherService {

    TeacherSummaryDto createTeacher(TeacherCreateDto dto);

    TeacherWithCoursesAndStudentsDto getTeacher(UUID id);

    TeacherSummaryDto upgradeTeacher(UUID id, TeacherUpgradeDto dto);

    void deleteTeacher(UUID id);

}

