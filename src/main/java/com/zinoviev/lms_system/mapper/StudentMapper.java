package com.zinoviev.lms_system.mapper;

import com.zinoviev.lms_system.dto.student.StudentCreateDto;
import com.zinoviev.lms_system.dto.student.StudentUpgradeDto;
import com.zinoviev.lms_system.dto.student.StudentWithGroupDto;
import com.zinoviev.lms_system.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    Student toEntity(StudentCreateDto dto);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    StudentWithGroupDto toResponseWithGroup(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    Student upgradeEntity(@MappingTarget Student oldStudent, StudentUpgradeDto dto);

}
