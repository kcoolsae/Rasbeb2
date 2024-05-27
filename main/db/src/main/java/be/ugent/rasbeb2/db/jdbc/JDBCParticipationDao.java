/*
 * JDBCParticipationDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class JDBCParticipationDao extends JDBCAbstractDao implements ParticipationDao {

    protected JDBCParticipationDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public int create(int contestId, int ageGroupId, String lang, int pupilId) {
        ContestWithAgeGroup contest = getContext().getContestDao().getContestWithAgeGroup(contestId, ageGroupId, lang);
        return insertInto("participations")
                .value("contest_id", contest.contestId())
                .value("pupil_id", pupilId)
                .value("age_group_id", contest.ageGroupId())
                .value("lang", lang)
                .value("participation_deadline", Instant.now().plus(contest.contestDuration(), ChronoUnit.MINUTES))
                .value("who_created", getUserId())
                .create();
    }

    @Override
    public int create(int eventId, int contestId, int ageGroupId, String lang, int pupilId) {
        ContestWithAgeGroup contest = getContext().getContestDao().getContestWithAgeGroup(contestId, ageGroupId, lang);
        return insertInto("participations")
                .value("event_id", eventId)
                .value("contest_id", contest.contestId())
                .value("pupil_id", pupilId)
                .value("age_group_id", contest.ageGroupId())
                .value("lang", lang)
                .value("participation_deadline", Instant.now().plus(contest.contestDuration(), ChronoUnit.MINUTES))
                .value("who_created", getUserId())
                .create();
    }

    private static Participation makeParticipation(ResultSet rs) throws SQLException {
        return new Participation(
                rs.getInt("contest_id"),
                rs.getInt("pupil_id"),
                rs.getInt("age_group_id"),
                rs.getString("lang"),
                rs.getInt("event_id"),
                rs.getBoolean("participation_closed"),
                rs.getInt("time_left_in_seconds"),
                rs.getInt("duration_in_seconds"),
                rs.getTimestamp("participation_deadline").toInstant()
        );
    }

    @Override
    public Participation get(int contestId, int pupilId) {
        return select("contest_id, pupil_id, age_group_id, lang, event_id, participation_closed, extract (epoch from (participation_deadline-now())) as time_left_in_seconds, participation_closed, (contest_duration * 60) as duration_in_seconds, participation_deadline")
                .from("participations JOIN contests_ag USING(contest_id, age_group_id)")
                .where("contest_id", contestId)
                .where("pupil_id", pupilId)
                .getOneObject(JDBCParticipationDao::makeParticipation);
    }

    @Override
    public void updateAnswer(int contestId, int pupilId, int questionId, String answer) {
        insertOrUpdateInto("participation_details")
                .key("contest_id", contestId)
                .key("pupil_id", pupilId)
                .key("question_id", questionId)
                .value("participation_answer", answer)
                .execute();
    }

    @Override
    public void close(int contestId, int pupilId) {
        update("participations").set("participation_closed", true)
                .where("contest_id", contestId)
                .where("pupil_id", pupilId)
                .execute();
    }

    @Override
    public void closeAndComputeMarks(int contestId, int pupilId) {
        call("close_participation(?,?,?)")
                .parameter(contestId)
                .parameter(pupilId)
                .parameter(getUserId())
                .execute();
    }

    private static ParticipationWithMarks makeParticipationMarks(ResultSet rs) throws SQLException {
        return new ParticipationWithMarks(
                rs.getInt("contest_id"),
                rs.getInt("pupil_id"),
                rs.getInt("age_group_id"),
                rs.getString("lang"),
                rs.getInt("participation_total_marks"),
                rs.getInt("max_marks")
        );
    }

    @Override
    public ParticipationWithMarks getMarks(int contestId, int pupilId) {
        return select("contest_id, pupil_id, age_group_id, lang, participation_total_marks, (max_marks - min_marks) as max_marks")
                .from("participations JOIN contests_ag USING(contest_id, age_group_id) JOIN question_sets USING(contest_id, age_group_id)")
                .where("contest_id", contestId)
                .where("pupil_id", pupilId)
                .getOneObject(JDBCParticipationDao::makeParticipationMarks);
    }

    private static QuestionWithFeedback makeQuestionWithMarks(ResultSet rs) throws SQLException {
        return new QuestionWithFeedback(
                rs.getInt("question_id"),
                rs.getString("question_title"),
                rs.getString("question_magic_q"),
                rs.getString("question_magic_f"),
                rs.getString("participation_answer"),
                rs.getInt("participation_marks"),
                rs.getInt("question_marks_if_correct")
        );
    }

    @Override
    public List<QuestionWithFeedback> getQuestionMarks(int contestId, int pupilId, int ageGroupId, String lang) {
        return select("q.question_id, question_title, question_magic_q, question_magic_f, participation_answer, participation_marks, question_marks_if_correct")
                .from("questions_in_set q JOIN questions USING(question_id) " +
                        "JOIN questions_i18n USING(question_id) " +
                        String.format("LEFT JOIN participation_details p ON(p.contest_id = q.contest_id AND p.question_id = q.question_id AND pupil_id = %d)", pupilId))
                .where("q.contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .where("lang", lang)
                .orderBy("question_seq_nr")
                .getList(JDBCParticipationDao::makeQuestionWithMarks);
    }
}
