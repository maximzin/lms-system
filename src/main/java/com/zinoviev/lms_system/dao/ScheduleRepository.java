package com.zinoviev.lms_system.dao;

import com.zinoviev.lms_system.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findAllByGroupId(UUID id);

    List<Schedule> findAllByTeacherId(UUID id);

    List<Schedule> findAllByGroupIdOrderByStartTimeAsc(UUID groupId);

    List<Schedule> findAllByTeacherIdOrderByStartTimeAsc(UUID teacherId);

}
