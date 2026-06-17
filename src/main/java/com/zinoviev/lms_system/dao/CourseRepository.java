package com.zinoviev.lms_system.dao;

import com.zinoviev.lms_system.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findAllByTeacherId(UUID teacherId);

    @Query("SELECT COUNT(c) > 0 FROM Course c JOIN c.groupList g " +
            "WHERE c.id = :courseId AND g.id = :groupId")
    boolean existsGroupInCourse(@Param("courseId") UUID courseId,
                                @Param("groupId") UUID groupId);
}
