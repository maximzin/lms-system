package com.zinoviev.lms_system.dao;

import com.zinoviev.lms_system.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findAllByGroupIdOrderByStartTimeAsc(UUID groupId);

    List<Schedule> findAllByTeacherIdOrderByStartTimeAsc(UUID teacherId);

    // Проверка пересечения у учителя для добавления записи
    @Query("""
        SELECT COUNT(s) > 0 FROM Schedule s
        WHERE s.teacher.id = :teacherId
          AND s.startTime < :endTime
          AND s.endTime > :startTime
    """)
    boolean existsOverlappingForTeacher(@Param("teacherId") UUID teacherId,
                                        @Param("startTime") ZonedDateTime startTime,
                                        @Param("endTime") ZonedDateTime endTime);

    // Проверка пересечения у учителя для обновления записи
    @Query("""
        SELECT COUNT(s) > 0 FROM Schedule s
        WHERE s.teacher.id = :teacherId
          AND s.startTime < :endTime
          AND s.endTime > :startTime
          AND s.id <> :excludeId
    """)
    boolean existsOverlappingForTeacherExcludingId(@Param("teacherId") UUID teacherId,
                                                   @Param("startTime") ZonedDateTime startTime,
                                                   @Param("endTime") ZonedDateTime endTime,
                                                   @Param("excludeId") UUID excludeId);
}

