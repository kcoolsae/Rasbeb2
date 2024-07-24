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

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest extends OrganiserDaoTest {

    // TODO clean up and extend

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
    }

}
