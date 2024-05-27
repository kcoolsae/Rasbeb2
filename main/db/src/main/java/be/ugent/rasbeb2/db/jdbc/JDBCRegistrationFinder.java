/*
 * JDBCRegistrationFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.RegistrationDao;
import be.ugent.rasbeb2.db.dto.Registration;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCRegistrationFinder extends JDBCAbstractFinder<Registration, Registration.Field, RegistrationDao.RegistrationFinder> implements RegistrationDao.RegistrationFinder{
    public JDBCRegistrationFinder(SelectSQLStatement stat) {
        super(stat);
    }

    @Override
    protected JDBCRegistrationFinder create(SelectSQLStatement stat) {
        return new JDBCRegistrationFinder(stat);
    }

    @Override
    protected Registration makeRecord(ResultSet rs) throws SQLException {
        return JDBCRegistrationDao.makeRegistration(rs);
    }

    public RegistrationDao.RegistrationFinder filter(Registration.Field field, String value) {
        return whereContains(field, value);
    }
}
