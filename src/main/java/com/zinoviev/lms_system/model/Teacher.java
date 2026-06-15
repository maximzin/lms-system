package com.zinoviev.lms_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teacher")
@Getter
@Setter
public class Teacher {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Course> courseList = new ArrayList<>();

    public Teacher() {
    }
}
