package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.teacher.TeacherCreateDto;
import com.zinoviev.lms_system.dto.teacher.TeacherWithCoursesAndStudentsDto;
import com.zinoviev.lms_system.dto.teacher.TeacherSummaryDto;
import com.zinoviev.lms_system.dto.teacher.TeacherUpgradeDto;
import com.zinoviev.lms_system.service.teacher.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@Tag(name = "Преподаватели", description = "Управление преподавателями")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @Operation(summary = "Создать преподавателя")
    public ResponseEntity<TeacherSummaryDto> createTeacher(@Valid @RequestBody TeacherCreateDto dto) {
        TeacherSummaryDto responseDto = teacherService.createTeacher(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
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
        return ResponseEntity.noContent().build();
    }

}
