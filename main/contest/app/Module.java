/*
 * Module.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

import be.ugent.rasbeb2.db.DataAccessProvider;
import be.ugent.rasbeb2.db.jdbc.JDBCDataAccessProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

import play.db.Database;

/**
 * Provides the injected database
 */
public class Module extends AbstractModule {

    @Provides
    @Singleton
    public DataAccessProvider createDataAccessProvider(Database database) {
        return new JDBCDataAccessProvider(database.getDataSource());
    }

}