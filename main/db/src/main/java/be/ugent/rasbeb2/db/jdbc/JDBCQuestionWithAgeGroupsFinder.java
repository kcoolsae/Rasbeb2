/*
 * JDBCQuestionWithAgeGroupsFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.QuestionWithAgeGroups;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class JDBCQuestionWithAgeGroupsFinder
        extends JDBCAbstractFinder<QuestionWithAgeGroups, QuestionWithAgeGroups.Field, QuestionDao.QuestionWithAgeGroupsFinder>
        implements QuestionDao.QuestionWithAgeGroupsFinder {

    public JDBCQuestionWithAgeGroupsFinder(SelectSQLStatement stat) {
        super(stat);
    }

    @Override
    public QuestionDao.QuestionWithAgeGroupsFinder filter(QuestionWithAgeGroups.Field field, String value) {
        return whereContains(field, value);
    }

    @Override
    protected JDBCQuestionWithAgeGroupsFinder create(SelectSQLStatement stat) {
        return new JDBCQuestionWithAgeGroupsFinder(stat);
    }

    @Override
    protected QuestionWithAgeGroups makeRecord(ResultSet rs) throws SQLException {
        return new QuestionWithAgeGroups(
                rs.getInt("question_id"),
                rs.getString("question_title"),
                rs.getString("question_external_id"),
                Arrays.stream((Integer[])rs.getArray("age_group_list").getArray()).filter(Objects::nonNull).toList()
        );
    }
}
