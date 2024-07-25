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
import be.ugent.rasbeb2.db.dao.PupilContestDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCPupilContestDao extends JDBCAbstractDao implements PupilContestDao {

    protected JDBCPupilContestDao(JDBCDataAccessContext context) {
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
                .getOneObject(JDBCPupilContestDao::makeContestsForPupilTable);
    }

    @Override
    public List<ContestForPupilTable> getContests(int pupilId) {
        return selectContest(pupilId)
                .getList(JDBCPupilContestDao::makeContestsForPupilTable);
    }

    private static ContestWithAgeGroup makeContestWithAgeGroup(ResultSet rs) throws SQLException {
        return new ContestWithAgeGroup(
                rs.getInt("contest_id"),
                rs.getString("contest_title"),
                rs.getInt("contest_duration"),
                rs.getInt("age_group_id"),
                rs.getString("age_group_name"),
                rs.getString("age_group_description")
        );
    }


    @Override
    public ContestWithAgeGroup getContestWithAgeGroup(int contestId, int ageGroupId, String lang) {
        return selectContestsWithAgeGroup(lang)
                .where("lang", lang)
                .where("contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .getOneObject(JDBCPupilContestDao::makeContestWithAgeGroup);
    }

    public SelectSQLStatement selectContestsWithAgeGroup(String lang) {
        return select("contest_id, contest_title, contest_duration, lang, age_group_id, age_group_name, age_group_description")
                .from("contests JOIN contests_ag USING(contest_id) JOIN age_groups USING(age_group_id) JOIN contests_i18n USING(contest_id, lang)")
                .where("lang", lang);
    }

    @Override
    public List<ContestForAnonTable> getOpenPublicContests(String lang) {
        List<ContestWithAgeGroup> contests = selectContestsWithAgeGroup(lang)
                .where("contest_type", ContestType.PUBLIC)
                .where("contest_status", ContestStatus.OPEN)
                .orderBy("contest_id", false)
                .getList(JDBCPupilContestDao::makeContestWithAgeGroup);

        int i = 0;
        List<ContestForAnonTable> contestForAnonTable = new ArrayList<>();
        while (i < contests.size()) {
            List<Integer> ageGroups = new ArrayList<>();
            int contestId = contests.get(i).contestId();
            int contestDuration = contests.get(i).contestDuration();
            String contestTitle = contests.get(i).contestTitle();
            while (i < contests.size() && contests.get(i).contestId() == contestId) {
                ageGroups.add(contests.get(i).ageGroupId());
                i++;
            }
            contestForAnonTable.add(new ContestForAnonTable(
                    contestId, contestTitle, contestDuration,
                    ageGroups
            ));
        }
        return contestForAnonTable;
    }

}
