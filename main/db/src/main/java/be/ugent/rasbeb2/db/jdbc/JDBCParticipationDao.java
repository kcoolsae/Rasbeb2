/*
 * JDBCParticipationDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCParticipationDao extends JDBCAbstractDao implements ParticipationDao {

    protected JDBCParticipationDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public void createParticipation(int contestId, int ageGroupId, String lang, int pupilId) {
        call ("create_anonymous_participation(?,?,?,?,?)")
                .parameter(contestId)
                .parameter(ageGroupId)
                .parameter(lang)
                .parameter(pupilId)
                .parameter(getUserId())
                .execute();
    }

    @Override
    public int createParticipation(int eventId) {
        int pupilId = getUserId();
        return select("create_participation(?,?,?)")
                .parameter(eventId)
                .parameter(pupilId)
                .parameter(pupilId)
                .noFrom()
                .getInt();
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
    public Participation getParticipation(int contestId) {
        return select("contest_id, pupil_id, age_group_id, lang, event_id, participation_closed, extract (epoch from (participation_deadline-now())) as time_left_in_seconds, participation_closed, (contest_duration * 60) as duration_in_seconds, participation_deadline")
                .from("participations JOIN contests_ag USING(contest_id, age_group_id)")
                .where("contest_id", contestId)
                .where("pupil_id", getUserId())
                .getOneObject(JDBCParticipationDao::makeParticipation);
    }

    public String[] getAnswerAndModel(int contestId, int pupilId, int questionId) {
        return select("participation_answer, participation_model")
                .from("participation_details")
                .where("contest_id", contestId)
                .where("pupil_id", pupilId)
                .where("question_id", questionId)
                .getObject(rs -> new String[]{
                        rs.getString("participation_answer"),
                        rs.getString("participation_model")
                });
    }

    @Override
    public void updateAnswerAndModel(int contestId, int pupilId, int questionId, String answer, String model) {
        insertOrUpdateInto("participation_details")
                .key("contest_id", contestId)
                .key("pupil_id", pupilId)
                .key("question_id", questionId)
                .value("participation_answer", answer)
                .value("participation_model", model)
                .execute();
    }

    @Override
    public void closeParticipation(int contestId) {
        update("participations").set("participation_closed", true)
                .where("contest_id", contestId)
                .where("pupil_id", getUserId())
                .execute();
    }

    @Override
    public void closeParticipationAndComputeMarks(int contestId) {
        call("close_participation(?,?,?)")
                .parameter(contestId)
                .parameter(getUserId())
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
    public ParticipationWithMarks getMarks(int contestId) {
        return select("contest_id, pupil_id, age_group_id, lang, participation_total_marks, (max_marks - min_marks) as max_marks")
                .from("participations JOIN contests_ag USING(contest_id, age_group_id) JOIN question_sets USING(contest_id, age_group_id)")
                .where("contest_id", contestId)
                .where("pupil_id", getUserId())
                .getObject(JDBCParticipationDao::makeParticipationMarks);
    }

    private static QuestionWithFeedback makeQuestionWithFeedback(ResultSet rs) throws SQLException {
        return new QuestionWithFeedback(
                rs.getInt("question_id"),
                AnswerType.valueOf(rs.getString("question_type")),
                rs.getString("question_title"),
                rs.getString("question_magic_q"),
                rs.getString("question_magic_f"),
                rs.getString("participation_answer"),
                rs.getString("participation_model"),
                rs.getInt("participation_marks"),
                rs.getInt("question_marks_if_correct"));
    }

    private SelectSQLStatement selectQuestionWithFeedback(int contestId, int ageGroupId, String lang) {
        return select("""
                      q.question_id, question_type, question_title, question_magic_q, question_magic_f,
                      participation_answer, participation_model,
                      participation_marks, question_marks_if_correct
                      """)
                .from("""
                      questions_in_set q JOIN questions USING(question_id)
                          JOIN questions_i18n USING(question_id)
                          LEFT JOIN participation_details p
                            ON(p.contest_id = q.contest_id AND p.question_id = q.question_id AND pupil_id = ?)
                      """)
                .parameter(getUserId())
                .where("q.contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .where("lang", lang);
    }

    @Override
    public List<QuestionWithFeedback> listQuestionsWithFeedback(int contestId, int ageGroupId, String lang) {
        return selectQuestionWithFeedback(contestId, ageGroupId, lang)
                .orderBy("question_seq_nr")
                .getList(JDBCParticipationDao::makeQuestionWithFeedback);
    }

    @Override
    public QuestionWithFeedback getQuestionWithFeedback(int contestId, int questionId, int ageGroupId, String lang) {
        return selectQuestionWithFeedback(contestId, ageGroupId, lang)
                .where("q.question_id", questionId)
                .getOneObject(JDBCParticipationDao::makeQuestionWithFeedback);
    }
}
