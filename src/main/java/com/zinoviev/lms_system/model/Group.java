package com.zinoviev.lms_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "study_group") // study_group из-за служебного слова group
@Getter
@Setter
public class Group {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Student> studentList = new ArrayList<>();

    @ManyToMany(mappedBy = "groupList")
    private List<Course> courseList = new ArrayList<>();

    public Group() {    }
}
