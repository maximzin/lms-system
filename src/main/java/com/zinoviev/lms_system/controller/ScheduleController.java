package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.schedule.*;
import com.zinoviev.lms_system.service.schedule.ScheduleService;
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
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
@Tag(name = "Расписание", description = "Управление расписанием")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Создать запись занятия")
    public ResponseEntity<ScheduleSummaryDto> createSchedule(@Valid @RequestBody ScheduleCreateDto dto) {
        ScheduleSummaryDto responseDto = scheduleService.createSchedule(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить время занятия")
    public ResponseEntity<ScheduleSummaryDto> updateSchedule(@PathVariable UUID id, @Valid @RequestBody ScheduleUpdateDto dto) {
        ScheduleSummaryDto responseDto = scheduleService.updateSchedule(id, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить назначенное занятие")
    public ResponseEntity<String> deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idGroup}")
    @Operation(summary = "Получить расписание занятий для группы по ID")
    public ResponseEntity<ScheduleForGroupDto> getScheduleForGroup(@PathVariable UUID idGroup) {
        ScheduleForGroupDto responseDto = scheduleService.getScheduleForGroup(idGroup);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{idTeacher}")
    @Operation(summary = "Получить расписание занятий для преподавателя по ID")
    public ResponseEntity<ScheduleForTeacherDto> getScheduleForTeacher(@PathVariable UUID idTeacher) {
        ScheduleForTeacherDto responseDto = scheduleService.getScheduleForTeacher(idTeacher);
        return ResponseEntity.ok(responseDto);
    }



}
