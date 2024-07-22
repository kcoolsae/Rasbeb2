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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Common super class of database tests run as an organiser. Provides a database
 * initialized with a single organiser which is also the user running the tests.
 */
abstract class OrganiserDaoTest extends DaoTest {

    // TODO work on a copy of the database

    protected DataAccessContext dac;

    @Before
    public void setup() {
        dac = DAP.getContext(1, 0, Role.ORGANISER);
        dac.begin();
    }

    @After
    public void tearDown() {
        dac.rollback();
        dac.close();
    }
}
