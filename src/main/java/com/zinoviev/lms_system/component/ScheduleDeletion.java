package com.zinoviev.lms_system.component;

import com.zinoviev.lms_system.service.schedule.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleDeletion {

    private final ScheduleService scheduleService;

    @Value("${schedule.duration-days-to-delete}")
    private Integer durationDaysToDelete;

    public ScheduleDeletion(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Scheduled(cron = "0 0 0 * * *") // каждый день в 00:00
    public void deleteOldSchedule() {
        scheduleService.deleteOldSchedule(durationDaysToDelete);
    }

}
