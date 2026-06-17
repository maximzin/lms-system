package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.student.StudentCreateDto;
import com.zinoviev.lms_system.dto.student.StudentUpgradeDto;
import com.zinoviev.lms_system.dto.student.StudentWithGroupDto;
import com.zinoviev.lms_system.service.student.StudentService;
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
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Студенты", description = "Управление студентами")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Создать студента")
    public ResponseEntity<StudentWithGroupDto> createStudent(@Valid @RequestBody StudentCreateDto dto) {
        StudentWithGroupDto responseDto = studentService.createStudent(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить студента по ID")
    public ResponseEntity<StudentWithGroupDto> getStudent(@PathVariable UUID id) {
        StudentWithGroupDto responseDto = studentService.getStudent(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить студента по ID")
    public ResponseEntity<StudentWithGroupDto> upgradeStudent(@PathVariable UUID id, @Valid @RequestBody StudentUpgradeDto dto) {
        StudentWithGroupDto responseDto = studentService.upgradeStudent(id, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить студента по ID")
    public ResponseEntity<String> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
