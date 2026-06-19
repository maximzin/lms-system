package com.zinoviev.lms_system.controller;

import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.course.*;
import com.zinoviev.lms_system.dto.exception.ExceptionDto;
import com.zinoviev.lms_system.dto.group.GroupSummaryDto;
import com.zinoviev.lms_system.exception.CourseNotFoundException;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestRestTemplate
class CourseControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("mydb")
            .withUsername("myuser")
            .withPassword("secret");

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;


    // Для создания курса нам нужен учитель
    @Autowired
    private TeacherRepository teacherRepository;

    private UUID teacherId;

    @Autowired
    private CourseRepository courseRepository;

    private UUID courseId;

    // Для проверки добавления группы к курсу
    @Autowired
    private GroupRepository groupRepository;


    @BeforeEach
    void init() {
        // Создаём учителя
        Teacher teacher = new Teacher();
        teacher.setFirstName("Olga");
        teacher.setLastName("Zinovieva");
        teacherId = teacherRepository.save(teacher).getId();

        // Создаём купс
        Course course = new Course();
        course.setName("Java Spring");
        course.setDescription("Java Spring for production");
        course.setTeacher(teacher);
        courseId = courseRepository.save(course).getId();
    }

    @AfterEach
    void tearDown() {
        // Чистим таблицы course и teacher
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("Создать курс, вернуть 201 статус")
    void createCourse_shouldCreateCourseAndReturn201() {
        // given
        CourseCreateDto courseCreateDto = new CourseCreateDto("Biology", "Course for beginners", teacherId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CourseCreateDto> entity = new HttpEntity<>(courseCreateDto, headers);

        // when
        ResponseEntity<CourseWithTeacherDto> responseDto = testRestTemplate
                .exchange(
                        "/api/course",
                        HttpMethod.POST,
                        entity,
                        CourseWithTeacherDto.class);

        // then
        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseDto.getBody()).isNotNull();
        assertThat(responseDto.getBody().name()).isEqualTo("Biology");
        assertThat(responseDto.getBody().teacherId()).isEqualTo(teacherId);
    }

    @Test
    @DisplayName("Получить курс по ID")
    void getCourse_shouldReturnCourse() {
        // given
        CourseWithTeacherDto expectedResponseDto = transactionTemplate.execute(status -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
            return new CourseWithTeacherDto(
                    course.getId(),
                    course.getName(),
                    course.getDescription(),
                    course.getTeacher().getId(),
                    course.getTeacher().getFirstName(),
                    course.getTeacher().getLastName()
            );
        });

        // when
        ResponseEntity<CourseWithTeacherDto> factResponseDto = testRestTemplate
                .getForEntity(
                        String.format("/api/course/%s", courseId),
                        CourseWithTeacherDto.class);

        // then
        assertThat(factResponseDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(factResponseDto.getBody()).isNotNull();
        assertThat(factResponseDto.getBody().id()).isEqualTo(courseId);
        assertThat(factResponseDto.getBody().name()).isEqualTo(expectedResponseDto.name());
        assertThat(factResponseDto.getBody().teacherFirstName()).isEqualTo(expectedResponseDto.teacherFirstName());
    }

    @Test
    @DisplayName("Обновить курс и вернуть обновленное представление")
    void upgradeCourse_shouldUpgradeCourseAndReturnUpgraded() {
        // given
        // Ещё один учитель
        Teacher teacher = new Teacher();
        teacher.setFirstName("Fedor");
        teacher.setLastName("Dvinyatin");
        UUID newTeacherId = teacherRepository.save(teacher).getId();

        CourseUpgradeDto courseUpgradeDto = new CourseUpgradeDto(
                "GO",
                "Course for beginners",
                newTeacherId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CourseUpgradeDto> entity = new HttpEntity<>(courseUpgradeDto, headers);


        // when
        ResponseEntity<CourseWithTeacherDto> newCourseDto = testRestTemplate
                .exchange(
                        String.format("/api/course/%s", courseId),
                        HttpMethod.PATCH,
                        entity,
                        CourseWithTeacherDto.class);

        // then
        assertThat(newCourseDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(newCourseDto.getBody()).isNotNull();
        assertThat(newCourseDto.getBody().name()).isEqualTo(courseUpgradeDto.name());
        assertThat(newCourseDto.getBody().teacherId()).isEqualTo(courseUpgradeDto.teacherId());
    }

    @Test
    @DisplayName("Не обновлять курс с незаполненным полем teacherId и вернуть 400")
    void upgradeCourse_shouldNotUpgradeCourseWithInvalidFieldAndReturn400() {
        // given
        CourseUpgradeDto courseUpgradeDto = new CourseUpgradeDto(
                "GO",
                "Course for beginners",
                null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CourseUpgradeDto> entity = new HttpEntity<>(courseUpgradeDto, headers);

        // when
        ResponseEntity<ExceptionDto> newCourseDto = testRestTemplate
                .exchange(
                        String.format("/api/course/%s", courseId),
                        HttpMethod.PATCH,
                        entity,
                        ExceptionDto.class);

        // then
        assertThat(newCourseDto.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(newCourseDto.getBody()).isNotNull();
        assertThat(newCourseDto.getBody().error()).isEqualTo("Невалидность полей");
        assertThat(newCourseDto.getBody().fieldErrors().containsKey("teacherId")).isTrue();
    }

    @Test
    @DisplayName("Удалить курс и вернуть 204 статус")
    void deleteCourse_shouldDeleteCourseAndReturn204() {
        // when
        testRestTemplate.delete(String.format("/api/course/%s", courseId));

        // then
        boolean isCourseExists = courseRepository.findById(courseId).isPresent();
        assertThat(isCourseExists).isFalse();
    }

    @Test
    @DisplayName("Добавить группу к курсу")
    void addGroupToCourse_shouldAddGroupToCourse() {
        // given
        // Создадим группу
        Group group = new Group();
        group.setName("group_1");
        UUID groupId = groupRepository.save(group).getId();

        AddGroupToCourseDto addGroupToCourseDto = new AddGroupToCourseDto(
                groupId,
                courseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddGroupToCourseDto> entity = new HttpEntity<>(addGroupToCourseDto, headers);

        // when
        ResponseEntity<CourseWithGroupsDto> responseDto = testRestTemplate
                .exchange(
                        "/api/course/add_group_to_course",
                        HttpMethod.POST,
                        entity,
                        CourseWithGroupsDto.class);

        // then
        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseDto.getBody()).isNotNull();
        assertThat(responseDto.getBody().id()).isEqualTo(courseId);
        assertThat(responseDto.getBody().groupList()).contains(new GroupSummaryDto(groupId, "group_1"));
    }

    @Test
    @DisplayName("Добавить несуществующую группу к курсу и вернуть 404 статус")
    void addGroupToCourse_shouldNotAddNonExistentGroupToCourseAndReturn404() {
        // given
        // Создадим случайный UUID несуществующей группы
        UUID nonExistentGroupId = UUID.randomUUID();

        AddGroupToCourseDto addGroupToCourseDto = new AddGroupToCourseDto(
                nonExistentGroupId,
                courseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddGroupToCourseDto> entity = new HttpEntity<>(addGroupToCourseDto, headers);

        // when
        ResponseEntity<ExceptionDto> responseDto = testRestTemplate
                .exchange(
                        "/api/course/add_group_to_course",
                        HttpMethod.POST,
                        entity,
                        ExceptionDto.class);

        // then
        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseDto.getBody()).isNotNull();
        assertThat(responseDto.getBody().message()).isEqualTo("Группа не найдена");
    }

    @Test
    @DisplayName("Убрать группу у курса и вернуть 200 статус")
    void removeGroupFromCourse_shouldRemoveGroupFromCourseAndReturn200() {
        // given
        // Создадим группу и добавим к курсу
        Group group = new Group();
        group.setName("group_1");
        UUID groupId = groupRepository.save(group).getId();
        transactionTemplate.execute(status -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
            course.getGroupList().add(group);
            courseRepository.save(course);
            return null;
        });

        RemoveGroupFromCourseDto removeGroupFromCourseDto = new RemoveGroupFromCourseDto(
                groupId,
                courseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RemoveGroupFromCourseDto> entity = new HttpEntity<>(removeGroupFromCourseDto, headers);


        // when
        ResponseEntity<String> responseDto = testRestTemplate
                .exchange(
                        "/api/course/remove_group_from_course",
                        HttpMethod.PATCH,
                        entity,
                        String.class);


        // then
        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Group> newGroupList = transactionTemplate.execute(status -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
            return new ArrayList<>(course.getGroupList());
        });
        assertThat(newGroupList).isNotNull();
        assertThat(newGroupList).doesNotContain(group);
    }
}