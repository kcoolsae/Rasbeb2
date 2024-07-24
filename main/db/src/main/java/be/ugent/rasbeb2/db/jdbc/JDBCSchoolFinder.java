/*
 * JDBCSchoolFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.SchoolDao;
import be.ugent.rasbeb2.db.dto.School;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCSchoolFinder extends JDBCAbstractFinder<School,School.Field, SchoolDao.SchoolFinder> implements SchoolDao.SchoolFinder {

    public JDBCSchoolFinder(SelectSQLStatement stat) {
        super(stat);
    }

    @Override
    protected JDBCSchoolFinder create(SelectSQLStatement stat) {
        return new JDBCSchoolFinder(stat);
    }

    @Override
    protected School makeRecord(ResultSet rs) throws SQLException {
        return JDBCSchoolDao.makeSchool(rs);
    }

    public SchoolDao.SchoolFinder filter(School.Field field, String value) {
        if (field == School.Field.ZIP) {
            return whereStartsWith(field, value);
        } else {
            return whereContains(field, value);
        }
    }
}
