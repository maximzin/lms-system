package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.teacher.TeacherCreateDto;
import com.zinoviev.lms_system.dto.teacher.TeacherWithCoursesAndStudentsDto;
import com.zinoviev.lms_system.dto.teacher.TeacherSummaryDto;
import com.zinoviev.lms_system.dto.teacher.TeacherUpgradeDto;
import com.zinoviev.lms_system.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/teacher")
@Tag(name = "Преподаватели", description = "Управление преподавателями")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @Operation(summary = "Создать преподавателя")
    public ResponseEntity<TeacherSummaryDto> createTeacher(@Valid @RequestBody TeacherCreateDto dto) {
        TeacherSummaryDto responseDto = teacherService.createTeacher(dto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить преподавателя по ID")
    public ResponseEntity<TeacherWithCoursesAndStudentsDto> getTeacher(@PathVariable UUID id) {
        TeacherWithCoursesAndStudentsDto responseDto = teacherService.getTeacher(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить преподавателя по ID")
    public ResponseEntity<TeacherSummaryDto> upgradeTeacher(@PathVariable UUID id, @Valid @RequestBody TeacherUpgradeDto dto) {
        TeacherSummaryDto responseDto = teacherService.upgradeTeacher(id, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить преподавателя по ID")
    public ResponseEntity<String> deleteTeacher(@PathVariable UUID id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok("Преподаватель был успешно удален");
    }

}
