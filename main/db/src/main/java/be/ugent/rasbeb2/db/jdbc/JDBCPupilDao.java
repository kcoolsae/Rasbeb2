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

    static Pupil makePupil(ResultSet rs) throws SQLException {
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
                JDBCEventDao.makeEvent(rs),
                rs.getBoolean("participation_closed"),
                rs.getTimestamp("participation_deadline")
        );
    }

    private SelectSQLStatement selectContest(int pupilId) {
        // TODO move to eventdao?
        return select("""
                    event_id, event_title, contest_title, events.lang, age_group_name,
                    event_status, contest_id, contest_status, contest_type,
                    participation_closed, participation_deadline
                    """)
                .from("""
                        permissions
                          JOIN events USING(event_id)
                          JOIN contests USING(contest_id)
                          LEFT JOIN contests_i18n USING(contest_id, lang)
                          LEFT JOIN contests_ag USING(contest_id, age_group_id)
                          JOIN age_groups USING(age_group_id, lang)
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
