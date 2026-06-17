package com.zinoviev.lms_system.service.schedule;

import com.zinoviev.lms_system.dto.schedule.*;

import java.util.UUID;

public interface ScheduleService {

    ScheduleSummaryDto createSchedule(ScheduleCreateDto dto);

    ScheduleSummaryDto updateSchedule(UUID scheduleId, ScheduleUpdateDto dto);

    void deleteSchedule(UUID id);

    ScheduleForGroupDto getScheduleForGroup(UUID groupId);

    ScheduleForTeacherDto getScheduleForTeacher(UUID teacherId);

}
