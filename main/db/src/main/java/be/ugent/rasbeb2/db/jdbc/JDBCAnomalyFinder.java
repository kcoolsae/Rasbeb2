/*
 * JDBCAnomalyFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.AnomalyFinder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class JDBCAnomalyFinder implements AnomalyFinder {

    private final SelectSQLStatement stat;

    public JDBCAnomalyFinder(SelectSQLStatement stat) {
        this.stat = stat;
    }

    private static Data makeData(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("participation_deadline");
        return new Data(
                rs.getInt("pupil_id"),
                rs.getString("pupil_name"),
                rs.getInt("school_id"),
                rs.getString("school_name"),
                rs.getString("school_town"),
                rs.getString("class_name"),
                timestamp == null ? null : timestamp.toInstant()
        );
    }

    @Override
    public List<Data> listAfterHour(int hour) {
        return stat
                .where("EXTRACT(HOUR FROM participation_deadline) >= ?", hour)
                .orderBy("school_id")
                .orderBy("pupil_name")
                .getList(JDBCAnomalyFinder::makeData);
    }

    @Override
    public List<Data> listInWeekend() {
        return stat
                .where("EXTRACT(DOW FROM participation_deadline) IN (0, 6)")
                .orderBy("school_id")
                .orderBy("pupil_name")
                .getList(JDBCAnomalyFinder::makeData);
    }

    @Override
    public List<Data> listAtDayOfMonth(int day) {
        return stat
                .where("EXTRACT(DAY FROM participation_deadline) = ?", day)
                .orderBy("school_id")
                .orderBy("pupil_name")
                .getList(JDBCAnomalyFinder::makeData);
    }
}
