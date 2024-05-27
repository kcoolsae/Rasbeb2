/*
 * DaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.jdbc.JDBCDataAccessProvider;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * Common super class for all tests of the Dao classes.
 */
abstract class DaoTest {

    protected DataAccessContext dac;

    protected static final DataAccessProvider DAP =
            new JDBCDataAccessProvider(getTestDataSource());

    /**
     * The role used when setting up the data access context. Can be overridden.
     */
    public Role getRole() {
        return null;
    }

    /**
     * The user ID used when setting up the data access context. Can be overridden.
     */
    public int getUserId() {
        return 0;
    }

    public int getSchoolId() {
        return 0;
    }

    @Before
    public void beginDaoTest() {
        dac = DAP.getContext(getUserId(),getSchoolId(), getRole());
        dac.begin();
    }

    @After
    public void endDaoTest() {
        dac.rollback();
        dac.close();
    }


    @SneakyThrows
    private static DataSource getTestDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        // runs on localhost
        dataSource.setDatabaseName("rasbeb2test");
        dataSource.setUser("rasbeb2");
        dataSource.setPassword("opensesame");
        dataSource.setProperty("escapeSyntaxCallMode","callIfNoReturn"); // throws SQLException
        return dataSource;
    }
}
