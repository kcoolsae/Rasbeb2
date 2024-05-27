/*
 * JDBCContestFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.Contest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCContestFinder extends JDBCAbstractFinder<Contest, Contest.Field, ContestDao.ContestFinder> implements ContestDao.ContestFinder{

    public JDBCContestFinder(SelectSQLStatement stat) {
        super(stat);
    }

    @Override
    protected JDBCContestFinder create(SelectSQLStatement stat) {
        return new JDBCContestFinder(stat);
    }

    @Override
    protected Contest makeRecord(ResultSet rs) throws SQLException {
        return JDBCContestDao.makeContest(rs);
    }

    public ContestDao.ContestFinder filter(Contest.Field field, String value) {
        return whereContains(field, value);
    }
}
