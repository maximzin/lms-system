package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.course.*;
import com.zinoviev.lms_system.service.course.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Курсы", description = "Управление курсами")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Создать курс")
    public ResponseEntity<CourseWithTeacherDto> createCourse(@Valid @RequestBody CourseCreateDto dto) {
        CourseWithTeacherDto responseDto = courseService.createCourse(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить курс по ID")
    public ResponseEntity<CourseWithTeacherDto> getCourse(@PathVariable UUID id) {
        CourseWithTeacherDto responseDto = courseService.getCourse(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(summary = "Получить курсы постранично, указывается номер страницы и количество")
    public ResponseEntity<Page<CourseWithTeacherDto>> getAllCourses(@PageableDefault(size = 15, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
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
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/groups")
    @Operation(summary = "Добавить группу к курсу")
    public ResponseEntity<CourseWithGroupsDto> addGroupToCourse(@PathVariable UUID courseId, @Valid @RequestBody AddGroupToCourseDto dto) {
        CourseWithGroupsDto responseDto = courseService.addGroupToCourse(courseId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{courseId}/groups/{groupId}")
    @Operation(summary = "Убрать группу с курса")
    public ResponseEntity<String> removeGroupFromCourse(@PathVariable UUID courseId, @PathVariable UUID groupId) {
        courseService.deleteGroupFromCourse(courseId, groupId);
        return ResponseEntity.noContent().build();
    }
    
}
