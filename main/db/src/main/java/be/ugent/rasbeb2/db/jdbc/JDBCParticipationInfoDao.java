/*
 * JDBCParticipationInfoDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ParticipationInfoDao;
import be.ugent.rasbeb2.db.dto.Gender;
import be.ugent.rasbeb2.db.dto.ParticipationInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class JDBCParticipationInfoDao extends JDBCAbstractDao implements ParticipationInfoDao {
    protected JDBCParticipationInfoDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public List<ParticipationInfo> getWinners(int contestId, int ageGroupId, int count) {
        return select("pupil_name, school_name, school_town, marks")
                .from("winners(?,?,?)")
                .parameter(contestId)
                .parameter(ageGroupId)
                .parameter(count)
                .getList(rs -> new ParticipationInfo(
                        rs.getString("pupil_name"),
                        rs.getString("school_name"),
                        rs.getString("school_town"),
                        rs.getInt("marks")
                ));
    }

    private static ParticipationInfo makeInfo(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("participation_deadline");
        return new ParticipationInfo(
                rs.getInt("pupil_id"),
                rs.getString("pupil_name"),
                rs.getInt("school_id"),
                rs.getString("school_name"),
                rs.getString("school_town"),
                rs.getString("class_name"),
                timestamp == null ? null : timestamp.toInstant()
        );
    }

    private static final String PARTICIPATION_INFO_JOIN = """
            participations
            join pupils using(pupil_id)
            join pupils_classes using(pupil_id)
            join events using(event_id)
            join classes using(class_id,school_id)
            join schools using(school_id)
            """;

    private SelectSQLStatement selectParticipationInfo(int contestId) {
        return select("""
                pupil_id, pupil_name, school_id, school_name, school_town, 
                class_name, participation_deadline
                """
        ).from(PARTICIPATION_INFO_JOIN)
                .where("participations.contest_id", contestId)
                .where("not participation_hidden");
    }

    @Override
    public List<ParticipationInfo> listAfterHour(int contestId, int hour) {
        return selectParticipationInfo(contestId)
                .where("EXTRACT(HOUR FROM participation_deadline) >= ?", hour)
                .orderBy("school_id")
                .orderBy("pupil_name")
                .getList(JDBCParticipationInfoDao::makeInfo);
    }

    @Override
    public List<ParticipationInfo> listInWeekend(int contestId) {
        return selectParticipationInfo(contestId)
                .where("EXTRACT(DOW FROM participation_deadline) IN (0, 6)")
                .orderBy("school_id")
                .orderBy("pupil_name")
                .getList(JDBCParticipationInfoDao::makeInfo);
    }

    @Override
    public List<ParticipationInfo> listAtDayOfMonth(int contestId, int day) {
        return selectParticipationInfo(contestId)
                .where("EXTRACT(DAY FROM participation_deadline) = ?", day)
                .orderBy("school_id")
                .orderBy("pupil_name")
                .getList(JDBCParticipationInfoDao::makeInfo);
    }

    private static ParticipationInfo makeInfoExtended(ResultSet rs) throws SQLException {
        return new ParticipationInfo(
                rs.getInt("pupil_id"),
                rs.getString("pupil_name"),
                Gender.valueOf(rs.getString("pupil_gender")),
                rs.getInt("age_group_id"),
                rs.getString("lang"),
                0,
                rs.getString("school_name"),
                rs.getString("school_town"),
                rs.getString("class_name"),
                null,
                rs.getInt("participation_total_marks")
        );
    }

    @Override
    public List<ParticipationInfo> listAll(int contestId) {
        return select("""
                pupil_id, pupil_name, pupil_gender, participations.lang,
                school_name, school_town,
                class_name, participations.age_group_id, participation_total_marks
                """).from(PARTICIPATION_INFO_JOIN)
                .where("participations.contest_id", contestId)
                .where("not participation_hidden")
                .orderBy("participations.age_group_id")
                .orderBy("participation_total_marks", false)
                .getList(JDBCParticipationInfoDao::makeInfoExtended);
    }

    @Override
    public List<Count> getCounts(int contestId) {
        return select("age_group_id, lang, count(*)")
                .from("participations")
                .where("contest_id", contestId)
                .groupBy("age_group_id, lang")
                .orderBy("age_group_id")
                .orderBy("lang")
                .getList(rs -> new Count(
                        rs.getInt("age_group_id"),
                        rs.getString("lang"),
                        rs.getInt("count")
                ));
    }
}
