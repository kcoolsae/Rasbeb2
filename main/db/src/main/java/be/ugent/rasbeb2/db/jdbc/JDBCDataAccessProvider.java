/*
 * JDBCDataAccessProvider.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.DACException;
import be.ugent.rasbeb2.db.DataAccessContext;
import be.ugent.rasbeb2.db.DataAccessProvider;
import be.ugent.rasbeb2.db.dto.Role;

import javax.sql.DataSource;
import java.sql.SQLException;

public class JDBCDataAccessProvider implements DataAccessProvider {

    private final DataSource dataSource;

    public JDBCDataAccessProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataAccessContext getContext(int userId, int schoolId, Role role) {
        try {
            return new JDBCDataAccessContext(dataSource.getConnection(), userId, schoolId, role);
        } catch (SQLException ex) {
            throw new DACException("Could not obtain connection from data source", ex);
        }
    }
}
