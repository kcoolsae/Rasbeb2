/*
 * OrganiserDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.OrganiserDao;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganiserDaoTest extends DaoTest {

    private OrganiserDao dao;

    @Before
    public void setupDao() {
        this.dao = dac.getOrganiserDao();
    }

    @Test
    public void initialTest() {
        assertThat (dao.noOrganisersRegistered()).isTrue();
    }

}
