/*
 * DaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.jdbc.JDBCDataAccessProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Common super class for all tests of the Dao classes.
 */
abstract class DaoTest {

    protected static DataAccessProvider DAP;

    private static final DataSource ORIGINAL_DATA_SOURCE = getDataSource("rasbeb2test");

    @BeforeAll
    static void cloneDatabase() throws SQLException {
        try (Connection connection = ORIGINAL_DATA_SOURCE.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE rasbeb2testclone WITH TEMPLATE rasbeb2test OWNER rasbeb2");
        }
        DAP = new JDBCDataAccessProvider(getDataSource("rasbeb2testclone"));
    }

    @AfterAll
    static void dropDatabase() throws SQLException {
        try (Connection connection = ORIGINAL_DATA_SOURCE.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE rasbeb2testclone");
        }
    }


    @SneakyThrows
    private static DataSource getDataSource(String dbName) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        // runs on localhost
        dataSource.setDatabaseName(dbName);
        dataSource.setUser("rasbeb2");
        dataSource.setPassword("opensesame");
        dataSource.setProperty("escapeSyntaxCallMode", "callIfNoReturn"); // throws SQLException
        return dataSource;
    }

}
