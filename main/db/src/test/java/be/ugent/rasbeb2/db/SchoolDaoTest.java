/*
 * SchoolDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.caagt.dao.NotFound;
import be.ugent.rasbeb2.db.dao.SchoolDao;
import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.dto.School;
import be.ugent.rasbeb2.db.dto.TeacherWithSchool;
import be.ugent.rasbeb2.db.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests school dao methods that are executed by an organiser
 */
class SchoolDaoTest extends OrganiserDaoTest {

    private SchoolDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getSchoolDao();
    }

    @Test
    void getSchool() {
        School school = dao.getSchool(1);
        assertThat(school).extracting(School::name, School::street, School::zip, School::town)
                .containsExactly("School, The", "Schoolstreet 1", "1234", "Rivendel");
    }

    @Test
    void editSchool() {
        dao.editSchool(1, "Another school", "Schoolstreet 123", "1234", "Somewhere");
        School school = dao.getSchool(1);
        assertThat(school).extracting(School::name, School::street, School::zip, School::town)
                .containsExactly("Another school", "Schoolstreet 123", "1234", "Somewhere");
    }

    @Test
    void createSchool() {
        int schoolId = dao.createSchool("Another school", "Schoolstreet 123", "1234", "Somewhere");
        School school = dao.getSchool(schoolId);
        assertThat(school).extracting(School::name, School::street, School::zip, School::town)
                .containsExactly("Another school", "Schoolstreet 123", "1234", "Somewhere");
    }

    @Test
    void findSchools() {
        List<School> schools = dao.findSchools()
                .getPageOrderedBy(School.Field.NAME, true, 0, 10)
                .getList();
        assertThat(schools).extracting(School::name)
                .containsExactly("College, The", "School, The");
    }

    @Test
    void removeSchool() {
        int schoolId = dao.createSchool("Another school", "Schoolstreet 123", "1234", "Somewhere");
        dao.getSchool(schoolId); // does not throw exception
        dao.removeSchool(schoolId);
        // but throws it now
        assertThatThrownBy(() -> dao.getSchool(schoolId))
                .isInstanceOf(NotFound.class);
    }

    // findSchools().filter()  not tested

    @Test
    void getSchoolId() {
        assertThat(dao.getSchoolId(2)).hasValue(1);
    }

    @Test
    void listAllTeachers() {
        List<User> teachers = dao.listAllTeachers(1, true);
        User teacher2 = new User (2, "Teacher, The", "teacher2@some.email.com", Role.TEACHER, false);
        User teacher3 = new User (3, "Teacher 3, The", "teacher3@some.email.com", Role.TEACHER, false);
        assertThat(teachers).containsExactly(teacher2, teacher3);
    }

    @Test
    void findTeachers() {
        List<TeacherWithSchool> teachers = dao.findTeachers()
                .getPageOrderedBy(TeacherWithSchool.Field.EMAIL, true, 0, 10)
                .getList();
        assertThat(teachers)
                .hasSize(3)
                .extracting(TeacherWithSchool::schoolId)
                .containsExactly(1, 1, 2);
    }

    @Test
    void findSomeTeachers() {
        List<TeacherWithSchool> teachers = dao.findTeachers()
                .filter(TeacherWithSchool.Field.SCHOOL_NAME, "College")
                .getPageOrderedBy(TeacherWithSchool.Field.EMAIL, true, 0, 10)
                .getList();
        assertThat(teachers)
                .extracting(TeacherWithSchool::name)
                .containsExactly("Teacher 4, The");
    }

    @Test
    void disableTeacher() {
        dao.disableTeacher(4);
        List<User> users = dao.listAllTeachers(2, true);
        assertThat(users).extracting(User::disabled).containsExactly(true);
        assertThat(dao.listAllTeachers(2, false)).isEmpty();
    }

    // test getSchool() with teacher as user (separate test class) tested in ClassesDaoTest
}
