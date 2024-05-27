/*
 * JDBCPupilDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.PupilDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCPupilDao extends JDBCAbstractDao implements PupilDao {

    protected JDBCPupilDao(JDBCDataAccessContext context) {
        super(context);
    }

    private static Pupil makePupil(ResultSet rs) throws SQLException {
        return new Pupil(
                rs.getInt("pupil_id"),
                rs.getString("pupil_name"),
                Gender.valueOf(rs.getString("pupil_gender")),
                rs.getString("pupil_password")
        );
    }

    @Override
    public int createAnonymousPupil() {
        return insertInto("pupils")
                .value("pupil_name", "")
                .value("pupil_password", "")
                .value("pupil_anonymous", true)
                .create();
    }

    @Override
    public Pupil getPupil(int pupilId, String password) {
        return select("pupil_id, pupil_name, pupil_gender, pupil_password")
                .from("pupils")
                .where("pupil_id", pupilId)
                .where("pupil_password", password)
                .getObject(JDBCPupilDao::makePupil);
    }

    private static ContestForPupilTable makeContestsForPupilTable(ResultSet rs) throws SQLException {
        return new ContestForPupilTable(
                new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_title"),
                        rs.getString("contest_title"),
                        EventStatus.valueOf(rs.getString("event_status")),
                        ContestStatus.valueOf(rs.getString("contest_status")),
                        ContestType.valueOf(rs.getString("contest_type")),
                        rs.getString("lang")
                ),
                rs.getInt("contest_id"),
                rs.getInt("age_group_id"),
                rs.getString("age_group_name"),
                rs.getBoolean("participation_closed"),
                rs.getTimestamp("participation_deadline")
        );
    }

    private SelectSQLStatement selectContest(int pupilId) {
        // TODO probably too much information
        return select("event_id, event_title, event_status, contest_id, contest_title, contest_status, contest_type, " +
                "age_group_id, age_group_name, participation_closed, participation_deadline, age_groups.lang")
                .from("""
                        permissions
                        JOIN events USING(event_id)
                        JOIN age_groups USING(age_group_id, lang)
                        JOIN contests USING(contest_id)
                        JOIN contests_i18n USING(contest_id, lang)
                        LEFT JOIN participations USING(event_id, pupil_id, contest_id, age_group_id)
                        """)
                .where("pupil_id", pupilId);
    }

    @Override
    public ContestForPupilTable getContest(int pupilId, int eventId) {
        return selectContest(pupilId)
                .where("event_id", eventId)
                .getOneObject(JDBCPupilDao::makeContestsForPupilTable);
    }

    @Override
    public List<ContestForPupilTable> getContests(int pupilId) {
        return selectContest(pupilId)
                .getList(JDBCPupilDao::makeContestsForPupilTable);
    }

}
