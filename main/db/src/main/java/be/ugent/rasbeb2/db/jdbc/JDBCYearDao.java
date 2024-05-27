/*
 * JDBCYearDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.OrderedSQLStatement;
import be.ugent.rasbeb2.db.dao.YearDao;
import be.ugent.rasbeb2.db.dto.Year;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCYearDao extends JDBCAbstractDao implements YearDao {

    protected JDBCYearDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public void createYear(String yearName) {
        insertInto("years")
                .value("year_name", yearName)
                .value("who_created", getUserId())
                .create();
    }
    @Override
    public void removeYear(int yearId) {
        deleteFrom("years").where("year_id", yearId).execute();
    }

    @Override
    public void updateYear(int yearId, String yearName) {
        update("years")
                .set("year_name", yearName)
                .set("who_updated", getUserId())
                .where("year_id", yearId)
                .execute();
    }


    static Year makeYear(ResultSet rs) throws SQLException {
        return new Year(
                rs.getInt("year_id"),
                rs.getString("year_name")
        );
    }

    private OrderedSQLStatement selectYear() {
        return select("year_id, year_name")
                .from("years").orderBy("year_id", false);
    }

    @Override
    public List<Year> listAllYears() {
        return selectYear().getList(JDBCYearDao::makeYear);
    }

    public Year getRecentYear() {
        return selectYear().onlyPage(0, 1).getObject(JDBCYearDao::makeYear);
    }

    public Year getYear(int yearId) {
        if (yearId == 0) {
            return getRecentYear();
        } else {
            return select("year_id, year_name")
                    .from("years")
                    .where("year_id", yearId)
                    .getObject(JDBCYearDao::makeYear);
        }
    }

}
