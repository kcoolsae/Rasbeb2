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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<ContestForPupilTable> getContests() {
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
                .where("pupil_id", getUserId())
                .getList(JDBCPupilContestDao::makeContestsForPupilTable);
    }

    private static ContestWithAgeGroup makeContestWithAgeGroup(ResultSet rs) throws SQLException {
        return new ContestWithAgeGroup(
                JDBCContestDao.makeContest(rs),
                JDBCAgeGroupDao.makeAgeGroupWithDuration(rs)
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
        return select("""
                contest_id, contest_type, contest_status, contest_title,
                contest_duration, age_group_id, age_group_name, age_group_description
                """)
                .from("contests JOIN contests_ag USING(contest_id) JOIN age_groups USING(age_group_id) JOIN contests_i18n USING(contest_id, lang)")
                .where("lang", lang);
    }

    @Override
    public Collection<ContestForAnonTable> getOpenPublicContests(String lang) {
        // all open public contests
        Map<Integer, ContestForAnonTable> map = select("contest_id, contest_title")// id must be first!
                .from("contests JOIN contests_i18n USING(contest_id)")
                .where("lang", lang)
                .where("contest_type", ContestType.PUBLIC)
                .where("contest_status", ContestStatus.OPEN)
                .orderBy("contest_id", false)
                .getMap(rs -> new ContestForAnonTable(
                                rs.getInt("contest_id"),
                                rs.getString("contest_title"),
                                new HashMap<>()
                        )
                );
        // related age group information
        select("contest_id, age_group_id, contest_duration")
                .from("contests JOIN contests_i18n USING(contest_id) JOIN contests_ag USING(contest_id)")
                .where("lang", lang)
                .where("contest_type", ContestType.PUBLIC)
                .where("contest_status", ContestStatus.OPEN)
                .processMap(map,
                        (c, rs) -> c.durations().put(
                                rs.getInt("age_group_id"),
                                rs.getInt("contest_duration")
                        )
                );
        return map.values();
    }

}
