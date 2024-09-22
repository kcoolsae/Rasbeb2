/*
 * UserDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.UserDao;
import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest extends OrganiserDaoTest {

    private UserDao dao;

    @BeforeEach
    void setupDao() {
        this.dao = dac.getUserDao();
    }

    @Test
    void user1isOrganiser () {
        User user = dao.getUser(1);
        assertThat(user).extracting(User::name, User::email, User::role)
                .containsExactly("Organiser, The", "organiser@some.email.com", Role.ORGANISER);
    }

    @Test
    void createTeacher() {
        int id = dao.createUser("John Doe", "john.doe@email.com", Role.TEACHER);
        User user = dao.getUser(id);
        assertThat(user).extracting(User::name, User::email, User::role)
                .containsExactly("John Doe", "john.doe@email.com", Role.TEACHER);
        dao.createTeacher(id, 2);
        List<User> teachers = dac.getSchoolDao().listAllTeachers(2, true);
        assertThat(teachers).extracting(User::name).contains("John Doe");
    }

    // createAnonymousPupil cannot easily  be tested
    // getPupil is tested in ClassesDaoTest editPupil and addPupil

    @Test
    void getUser() {
        User user = dao.getUser("teacher2@some.email.com", "Opensesame");
        assertThat(user).extracting(User::name).isEqualTo("Teacher, The");
    }

    @Test
    void getUserWrongPassword() {
        User user = dao.getUser("teacher2@some.email.com", "Closesesame");
        assertThat(user).isNull();
    }

    @Test
    void getUserWrongEmail() {
        User user = dao.getUser("teacher2@email.com", "Opensesame");
        assertThat(user).isNull();
    }

    @Test
    void updatePassword() {
        dao.updatePassword(2, "newpassword");
        User user = dao.getUser("teacher2@some.email.com", "newpassword");
        assertThat(user.name()).isEqualTo("Teacher, The");
    }

    @Test
    void updateName() {
        dao.updateUsername("Teacher, That");
        User user = dao.getUser(1);
        assertThat(user).extracting(User::name).isEqualTo("Teacher, That");
    }

    @Test
    void getUserId() {
        int id = dao.getUserId("teacher3@some.email.com");
        assertThat(id).isEqualTo(3);
    }

    @Test
    void getUser2() {
        User user = dao.getUser(4);
        assertThat(user).extracting(User::name, User::email, User::role)
                .containsExactly("Teacher 4, The", "teacher4@some.email.com", Role.TEACHER);
    }

    @Test
    void isKnownEmailAddress() {
        assertThat (dao.isKnownEmailAddress("teacher4@some.email.com")).isTrue();
        assertThat (dao.isKnownEmailAddress("4teacher@some.email.com")).isFalse();
    }


}
