/*
 * JDBCQuestionDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class JDBCQuestionDao extends JDBCAbstractDao implements QuestionDao {
    protected JDBCQuestionDao(JDBCDataAccessContext context) {
        super(context);
    }

    static QuestionHeader makeQuestionHeader(ResultSet rs) throws SQLException {
        return new QuestionHeader(
                rs.getInt("question_id"),
                AnswerType.valueOf(rs.getString("question_type")),
                rs.getString("question_title"),
                rs.getString("question_external_id")
        );
    }

    static Question makeQuestion(ResultSet rs) throws SQLException {
        return new Question(
                makeQuestionHeader(rs),
                rs.getString("question_type_xtra"),
                rs.getString("question_magic_q"),
                rs.getString("question_magic_f")
        );
    }

    private static final String JOIN_CLAUSE =
            "questions LEFT JOIN questions_i18n ON questions.question_id = questions_i18n.question_id AND lang = ?";

    /**
     * Selects question information with title in the given language
     */
    private SelectSQLStatement selectQuestionHeader(String lang) {
        return select("questions.question_id, question_type, question_external_id, question_title")
                .from(JOIN_CLAUSE)
                .parameter(lang);
    }

    @Override
    public QuestionFinder findQuestions(String lang) {
        return new JDBCQuestionFinder(selectQuestionHeader(lang));
    }

    @Override
    public QuestionWithAgeGroupsFinder findQuestionsWithAgeGroups(int contestId, String lang) {
        // uses stored procedure
        return new JDBCQuestionWithAgeGroupsFinder(
                select("question_id, question_title, question_external_id, age_group_list")
                        .from("questionsWithAgeGroups(?)")
                        .parameter(contestId)
                        .where("lang", lang)
        );
    }

    @Override
    public int createQuestion(AnswerType type, String typeExtra, String externalId) {
        return insertInto("questions")
                .value("question_type", type)
                .value("question_type_xtra", typeExtra)
                .value("question_external_id", externalId)
                .value("who_created", getUserId())
                .create();
    }

    @Override
    public void addTranslation(int questionId, Translation translation) {
        insertInto("questions_i18n")
                .value("question_id", questionId)
                .value("lang", translation.lang())
                .value("question_title", translation.title())
                .value("question_correct_answer", translation.correctAnswer())
                .value("who_updated", getUserId())
                .execute();
    }

    @Override
    public Question getQuestion(int questionId, String lang) {
        return select("""
                    questions.question_id, question_type, question_external_id, question_title,
                    question_type_xtra, question_magic_q, question_magic_f
                """)
                .from(JOIN_CLAUSE)
                .parameter(lang)
                .where("questions.question_id", questionId)
                .getOneObject(JDBCQuestionDao::makeQuestion);
    }

    @Override
    public List<Question> getQuestionsForContest(int contestId, int ageGroupId, String lang) {
        return select("question_id, question_title, question_external_id, question_magic_q, question_type, question_type_xtra, question_magic_f")
                .from("questions_in_set JOIN questions USING(question_id) JOIN questions_i18n USING(question_id)")
                .where("contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .where("lang", lang)
                .orderBy("question_seq_nr")
                .getList(JDBCQuestionDao::makeQuestion);
    }

    @Override
    public void editTranslation(int questionId, String lang, String newTitle, String newSolution) {
        update("questions_i18n")
                .set("question_title", newTitle)
                .set("question_correct_answer", newSolution)
                .set("who_updated", getUserId())
                .where("question_id", questionId)
                .where("lang", lang)
                .execute();
    }

    @Override
    public void addTranslation(int questionId, String lang) {
        insertInto("questions_i18n")
                .value("question_id", questionId)
                .value("lang", lang)
                .value("who_updated", getUserId())
                .execute();
    }

    @Override
    public void removeTranslation(int questionId, String lang) {
        deleteFrom("questions_i18n")
                .where("question_id", questionId)
                .where("lang", lang)
                .execute();
    }

    private static QuestionI18n makeQuestionI18n(ResultSet rs) throws SQLException {
        return new QuestionI18n(
                rs.getInt("question_id"),
                rs.getString("lang"),
                rs.getString("question_title"),
                rs.getString("question_correct_answer"),
                rs.getBoolean("question_uploaded_q"),
                rs.getBoolean("question_uploaded_f")
        );
    }

    @Override
    public List<QuestionI18n> getQuestionI18n(int questionId) {
        return select("question_id, question_title, lang, question_correct_answer, question_uploaded_q, question_uploaded_f")
                .from("questions JOIN questions_i18n USING(question_id)")
                .where("question_id", questionId)
                .orderBy("lang")
                .getList(JDBCQuestionDao::makeQuestionI18n);
    }

    @Override
    public void editQuestion(int questionId, AnswerType type, String typeExtra, String externalId) {
        update("questions")
                .set("question_type", type)
                .set("question_type_xtra", typeExtra)
                .set("question_external_id", externalId)
                .set("who_updated", getUserId())
                .where("question_id", questionId)
                .execute();
    }

    @Override
    public boolean questionExists(String externalId) {
        return !select("").from("questions").where("question_external_id", externalId).isEmpty();
    }

    @Override
    public void setQuestionAgeGroups(int contestId, int questionId, List<Integer> ageGroups) {
        Integer[] array = ageGroups.stream().filter(Objects::nonNull).toArray(Integer[]::new); // ugly!
        call("setQuestionAgeGroups(?,?,?,?)")
                .parameter(contestId)
                .parameter(questionId)
                .parameter(array)
                .parameter(getUserId())
                .execute();
    }

    @Override
    public boolean questionAlreadyUploaded(int questionId, String lang) {
        return !select("1")
                .from("questions_i18n")
                .where("question_id", questionId)
                .where("lang", lang)
                .where("(question_uploaded_q OR question_uploaded_f)")  // parentheses are needed here!
                .isEmpty();
    }

    @Override
    public void setUploaded(int questionId, String lang, FileType fileType) {
        update("question_i18n")
                .set("question_uploaded_" + fileType.name().charAt(0) + "=true")
                .set("who_updated", getUserId())
                .where("question_id", questionId)
                .where("lang", lang)
                .execute();
    }

    @Override
    public String getMagic(int questionId, FileType fileType) {
        return select("question_magic_" + fileType.name().charAt(0))
                .from("questions")
                .where("question_id", questionId)
                .getOneString();
    }
}
