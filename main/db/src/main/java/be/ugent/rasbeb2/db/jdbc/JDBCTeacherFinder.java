/*
 * JDBCTeacherFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.TeacherWithSchool;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCTeacherFinder extends JDBCAbstractFinder<TeacherWithSchool, TeacherWithSchool.Field, ClassesDao.TeacherFinder> implements ClassesDao.TeacherFinder {

    public JDBCTeacherFinder(SelectSQLStatement stat) {
        super(stat);
    }

    @Override
    protected JDBCTeacherFinder create(SelectSQLStatement stat) {
        return new JDBCTeacherFinder(stat);
    }

    @Override
    protected TeacherWithSchool makeRecord(ResultSet rs) throws SQLException {
        return JDBCClassesDao.makeTeachersWithSchool(rs);
    }

    public ClassesDao.TeacherFinder filter(TeacherWithSchool.Field field, String value) {
        return whereContains(field, value);
    }
}
