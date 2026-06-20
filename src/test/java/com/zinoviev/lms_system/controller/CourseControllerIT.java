package com.zinoviev.lms_system.controller;

import com.jayway.jsonpath.JsonPath;
import com.zinoviev.lms_system.dao.CourseRepository;
import com.zinoviev.lms_system.dao.GroupRepository;
import com.zinoviev.lms_system.dao.TeacherRepository;
import com.zinoviev.lms_system.dto.course.*;
import com.zinoviev.lms_system.dto.exception.ExceptionDto;
import com.zinoviev.lms_system.dto.group.GroupSummaryDto;
import com.zinoviev.lms_system.exception.CourseNotFoundException;
import com.zinoviev.lms_system.exception.TeacherNotFoundException;
import com.zinoviev.lms_system.model.Course;
import com.zinoviev.lms_system.model.Group;
import com.zinoviev.lms_system.model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourseControllerIT  extends AbstractIT {

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
                        "/api/v1/courses",
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
    @DisplayName("Запросить курс по ID и получить 200 статус")
    void getCourse_shouldGetCourseAndReturn200() {
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
                        String.format("/api/v1/courses/%s", courseId),
                        CourseWithTeacherDto.class);

        // then
        assertThat(factResponseDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(factResponseDto.getBody()).isNotNull();
        assertThat(factResponseDto.getBody().id()).isEqualTo(courseId);
        assertThat(expectedResponseDto).isNotNull();
        assertThat(factResponseDto.getBody().name()).isEqualTo(expectedResponseDto.name());
        assertThat(factResponseDto.getBody().teacherFirstName()).isEqualTo(expectedResponseDto.teacherFirstName());
    }

    @Test
    @DisplayName("Запросить курсы через пагинацию и получить 200 статус")
    void getAllCourses_shouldGetCoursesAndReturn200() {
        // given
        // Создадим еще несколько курсов
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new TeacherNotFoundException("Учитель не найден"));
        for (int i = 2; i <= 30; i++) {
            Course newCourse = new Course();
            newCourse.setName("course_" + i);
            newCourse.setDescription("course_desc_" + i);
            newCourse.setTeacher(teacher);
            courseRepository.save(newCourse);
        }
        int queryParamPage = 0;
        int queryParamSize = 5;
        String queryParamSort = "name,asc";

        String url = UriComponentsBuilder.fromUriString("/api/v1/courses")
                .queryParam("page", queryParamPage)
                .queryParam("size", queryParamSize)
                .queryParam("sort", queryParamSort)
                .toUriString();

        // when
        // Получаем ответ как строку,
        // потому что с responseType: Page<CourseWithTeacherDto> restTemplate работать не может
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        // then: проверяем HTTP-статус
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверяем тело с помощью JsonPath
        String body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(JsonPath.<Integer>read(body, "$.size")).isEqualTo(queryParamSize);
        assertThat(JsonPath.<Integer>read(body, "$.number")).isEqualTo(queryParamPage);
        assertThat(JsonPath.<Integer>read(body, "$.totalElements")).isGreaterThanOrEqualTo(30);
        assertThat(JsonPath.<List<?>>read(body, "$.content")).hasSize(queryParamSize);
        assertThat(JsonPath.<String>read(body, "$.content[0].description")).isNotNull();
        assertThat(JsonPath.<String>read(body, "$.content[0].teacherFirstName")).isNotNull();
    }

    @Test
    @DisplayName("Обновить курс и вернуть 200 статус")
    void upgradeCourse_shouldUpgradeCourseAndReturn200() {
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
                        String.format("/api/v1/courses/%s", courseId),
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
    @DisplayName("Не обновлять курс с незаполненным полем teacherId и вернуть 400 статус")
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
                        String.format("/api/v1/courses/%s", courseId),
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
        testRestTemplate.delete(String.format("/api/v1/courses/%s", courseId));

        // then
        boolean isCourseExists = courseRepository.findById(courseId).isPresent();
        assertThat(isCourseExists).isFalse();
    }

    @Test
    @DisplayName("Добавить группу к курсу и вернуть 201 статус")
    void addGroupToCourse_shouldAddGroupToCourseAndReturn201() {
        // given
        // Создадим группу
        Group group = new Group();
        group.setName("group_1");
        UUID groupId = groupRepository.save(group).getId();

        AddGroupToCourseDto addGroupToCourseDto = new AddGroupToCourseDto(groupId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddGroupToCourseDto> entity = new HttpEntity<>(addGroupToCourseDto, headers);

        // when
        ResponseEntity<CourseWithGroupsDto> responseDto = testRestTemplate
                .exchange(
                        String.format("/api/v1/courses/%s/groups", courseId),
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

        AddGroupToCourseDto addGroupToCourseDto = new AddGroupToCourseDto(nonExistentGroupId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddGroupToCourseDto> entity = new HttpEntity<>(addGroupToCourseDto, headers);

        // when
        ResponseEntity<ExceptionDto> responseDto = testRestTemplate
                .exchange(
                        String.format("/api/v1/courses/%s/groups", courseId),
                        HttpMethod.POST,
                        entity,
                        ExceptionDto.class);

        // then
        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseDto.getBody()).isNotNull();
        assertThat(responseDto.getBody().message()).isEqualTo("Группа не найдена");
    }

    @Test
    @DisplayName("Убрать группу у курса и вернуть 204 статус")
    void removeGroupFromCourse_shouldRemoveGroupFromCourseAndReturn204() {
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

        // when
        testRestTemplate.delete(String.format("/api/v1/courses/%s/groups/%s", courseId, groupId));

        // then
        List<Group> newGroupList = transactionTemplate.execute(status -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new CourseNotFoundException("Курс не найден"));
            return new ArrayList<>(course.getGroupList());
        });
        assertThat(newGroupList).isNotNull();
        assertThat(newGroupList).doesNotContain(group);
    }
}