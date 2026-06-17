package com.zinoviev.lms_system.dto.group;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UniteStudentsInGroupDto(

        @NotNull
        List<UUID> studentIdList,

        @NotNull
        UUID groupId

) {
}
