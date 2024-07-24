/*
 * AsOrganiserTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dto.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Common super class of database tests run as a teacher (user 2 of school 1).
 */
abstract class TeacherDaoTest extends DaoTest {

    protected DataAccessContext dac;

    @BeforeEach
    void setupDac() {
        dac = DAP.getContext(2, 1, Role.TEACHER);
        dac.begin();
    }

    @AfterEach
    void tearDownDac() {
        dac.rollback();
        dac.close();
    }
}
