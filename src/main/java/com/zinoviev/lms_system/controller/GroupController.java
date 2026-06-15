package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dto.group.*;
import com.zinoviev.lms_system.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/group")
@Tag(name = "Группы", description = "Управление группами")
public class GroupController {
    
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @Operation(summary = "Создать группу")
    public ResponseEntity<GroupSummaryDto> createGroup(@Valid @RequestBody GroupCreateDto dto) {
        GroupSummaryDto responseDto = groupService.createGroup(dto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить группу по ID")
    public ResponseEntity<GroupWithStudentsDto> getGroup(@PathVariable UUID id) {
        GroupWithStudentsDto responseDto = groupService.getGroup(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить группу по ID")
    public ResponseEntity<GroupSummaryDto> upgradeGroup(@PathVariable UUID id, @Valid @RequestBody GroupUpgradeDto dto) {
        GroupSummaryDto responseDto = groupService.upgradeGroup(id, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить группу по ID")
    public ResponseEntity<String> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok("Группа была успешно удалена");
    }

    @PatchMapping("/unite_students_in_group")
    @Operation(summary = "Объединить список студентов в группу")
    public ResponseEntity<GroupWithStudentsDto> uniteStudentsInGroup(@Valid @RequestBody UniteStudentsInGroupDto dto) {
        GroupWithStudentsDto responseDto = groupService.uniteStudentsInGroup(dto);
        return ResponseEntity.ok(responseDto);
    }

}
