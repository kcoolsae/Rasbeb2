/*
 * JDBCQuestionFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.QuestionHeader;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCQuestionFinder extends JDBCAbstractFinder<QuestionHeader, QuestionHeader.Field, QuestionDao.QuestionFinder> implements QuestionDao.QuestionFinder{

    public JDBCQuestionFinder(SelectSQLStatement stat) {
        super(stat);
    }

    @Override
    protected JDBCQuestionFinder create(SelectSQLStatement stat) {
        return new JDBCQuestionFinder(stat);
    }

    @Override
    protected QuestionHeader makeRecord(ResultSet rs) throws SQLException {
        return JDBCQuestionDao.makeQuestionHeader(rs);
    }

    public QuestionDao.QuestionFinder filter(QuestionHeader.Field field, String value) {
        return whereContains(field, value);
    }
}
