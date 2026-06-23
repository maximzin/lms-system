package com.zinoviev.lms_system.component;

import com.zinoviev.lms_system.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleDeletion {

    private final ScheduleService scheduleService;

    @Value("${schedule.duration-days-to-delete}")
    private Integer durationDaysToDelete;

    @Scheduled(cron = "${schedule.deletion-period-cron}")
    public void deleteOldSchedule() {
        scheduleService.deleteOldSchedule(durationDaysToDelete);
    }

}
