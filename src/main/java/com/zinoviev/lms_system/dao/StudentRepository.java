package com.zinoviev.lms_system.dao;

import com.zinoviev.lms_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    List<Student> findAllByGroupId(UUID groupId);

    @Query("SELECT s FROM Student s WHERE s.group.id IN " +
            "(SELECT cgl.id FROM Course c JOIN c.groupList cgl WHERE c.teacher.id = :teacherId)")
    List<Student> findAllByTeacherId(@Param("teacherId") UUID teacherId);
}
