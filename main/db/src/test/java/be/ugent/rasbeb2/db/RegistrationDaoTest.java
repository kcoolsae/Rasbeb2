/*
 * RegistrationDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.RegistrationDao;
import be.ugent.rasbeb2.db.dto.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationDaoTest extends OrganiserDaoTest {

    private RegistrationDao dao;

    @BeforeEach
    void setupDao() {
        this.dao = dac.getRegistrationDao();
    }

    @Test
    void addPasswordRequest() {
        String email = "johndoe@email.com";
        String token = dao.addPasswordRequest(email);
        assertThat (dao.isValidPasswordRequest(email, token)).isTrue();
        assertThat (dao.isValidPasswordRequest("janedoe@email.com", token)).isFalse();
        assertThat (dao.isValidPasswordRequest(email, "definitely not a token")).isFalse();

        // difficult to test expiration date
    }

    @Test
    void deletePasswordRequest() {
        String email = "johndoe@email.com";
        String token = dao.addPasswordRequest(email);
        dao.deletePasswordRequest(email);
        assertThat(dao.isValidPasswordRequest(email, token)).isFalse();
    }

    @Test
    void passwordRequestExpired() {
        String email = "teacher2@some.email.com";
        String token = "some token";
        assertThat(dao.isValidPasswordRequest(email, token)).isFalse();
    }

    @Test
    void addRegistration() {
        String email = "newTeacher@some.email.com";
        String token = dao.addRegistration(email, 1);
        assertThat(dao.isValidRegistration(email, token, 1)).isTrue();
        assertThat(dao.isValidRegistration(email, token, 2)).isFalse();
        assertThat(dao.isValidRegistration("not-a-user", token, 1)).isFalse();
        assertThat(dao.isValidRegistration(email, "not-a-token", 1)).isFalse();
    }

    @Test
    void renewRegistration() {
        String email = "newuser@some.email.com"; // expired request
        String token = dao.addRegistration(email, 1);
        assertThat(dao.isValidRegistration(email, token, 1)).isTrue();
    }

    @Test
    void deleteRegistration() {
        String email = "johndoe@email.com";
        String token = dao.addRegistration(email, 1);
        dao.deleteRegistration(email);
        assertThat(dao.isValidRegistration(email, token, 1)).isFalse();
    }

    @Test
    void deleteExpiredRegistrations() {
        dao.deleteExpiredRegistrations();
        int remaining = dao.findRegistrations()
                .getPageOrderedBy(Registration.Field.EMAIL, true, 0, 1)
                .getFullSize();
        assertThat(remaining).isEqualTo(0);
    }

    @Test
    void findRegistrations() {
        List<Registration> registrations = dao.findRegistrations()
                .getPageOrderedBy(Registration.Field.EMAIL, true, 0, 1)
                .getList();
        assertThat(registrations).extracting(Registration::email)
                .containsExactly("newuser@some.email.com");
    }

    @Test
    void registrationExpired() {
        String email = "newuser@some.email.com";
        String token = "some token";
        assertThat(dao.isValidPasswordRequest(email, token)).isFalse();
    }

    // findRegistrations().filter()  not tested

}
