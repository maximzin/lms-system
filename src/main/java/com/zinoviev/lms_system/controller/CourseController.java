package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.course.*;
import com.zinoviev.lms_system.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/course")
@Tag(name = "Курсы", description = "Управление курсами")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(summary = "Создать курс")
    public ResponseEntity<CourseWithTeacherDto> createCourse(@Valid @RequestBody CourseCreateDto dto) {
        CourseWithTeacherDto responseDto = courseService.createCourse(dto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить курс по ID")
    public ResponseEntity<CourseWithTeacherDto> getCourse(@PathVariable UUID id) {
        CourseWithTeacherDto responseDto = courseService.getCourse(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить курс по ID")
    public ResponseEntity<CourseWithTeacherDto> upgradeCourse(@PathVariable UUID id, @Valid  @RequestBody CourseUpgradeDto dto) {
        CourseWithTeacherDto responseDto = courseService.upgradeCourse(id, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить курс по ID")
    public ResponseEntity<String> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Группа была успешно удалена");
    }

    @PostMapping("/add_group_to_course")
    @Operation(summary = "Добавить группу к курсу")
    public ResponseEntity<CourseWithGroupsDto> addGroupToCourse(@Valid @RequestBody AddGroupToCourseDto dto) {
        CourseWithGroupsDto responseDto = courseService.addGroupToCourse(dto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/remove_group_from_course")
    @Operation(summary = "Убрать группу с курса")
    public ResponseEntity<String> removeGroupFromCourse(@Valid @RequestBody RemoveGroupFromCourseDto dto) {
        courseService.deleteGroupFromCourse(dto);
        return ResponseEntity.ok("Группа была удалена с курса");
    }
    
}
