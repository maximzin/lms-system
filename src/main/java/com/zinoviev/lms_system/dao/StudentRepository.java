package com.zinoviev.lms_system.dao;

import com.zinoviev.lms_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    List<Student> findAllByGroupId(UUID groupId);
}
