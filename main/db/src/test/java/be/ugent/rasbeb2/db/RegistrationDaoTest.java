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
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationDaoTest extends DaoTest {

    private RegistrationDao dao;

    @Before
    public void setupDao() {
        this.dao = dac.getRegistrationDao();
    }

    @Test
    public void test() {
        String email = "johndoe@email.com";
        String token = dao.addPasswordRequest(email);
        assertThat (dao.isValidPasswordRequest(email, token)).isTrue();
        assertThat (dao.isValidPasswordRequest("janedoe@email.com", token)).isFalse();
        assertThat (dao.isValidPasswordRequest(email, "definitely not a token")).isFalse();

        // difficult to test expiration date
    }
}
