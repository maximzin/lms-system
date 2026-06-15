package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.group.GroupWithStudentsDto;
import com.zinoviev.lms_system.dto.schedule.*;
import com.zinoviev.lms_system.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Расписание", description = "Управление расписанием")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @Operation(summary = "Создать запись занятия")
    public ResponseEntity<ScheduleSummaryDto> createSchedule(@Valid @RequestBody ScheduleCreateDto dto) {
        ScheduleSummaryDto responseDto = scheduleService.createSchedule(dto);
        return ResponseEntity.ok(responseDto);
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
        return ResponseEntity.ok("Запись расписания была успешно удалена");
    }

    @GetMapping("/for_group/{idGroup}")
    @Operation(summary = "Получить расписание занятий для группы по ID")
    public ResponseEntity<ScheduleForGroupDto> getScheduleForGroup(@PathVariable UUID idGroup) {
        ScheduleForGroupDto responseDto = scheduleService.getScheduleForGroup(idGroup);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/for_teacher/{idTeacher}")
    @Operation(summary = "Получить расписание занятий для преподавателя по ID")
    public ResponseEntity<ScheduleForTeacherDto> getScheduleForTeacher(@PathVariable UUID idTeacher) {
        ScheduleForTeacherDto responseDto = scheduleService.getScheduleForTeacher(idTeacher);
        return ResponseEntity.ok(responseDto);
    }



}
