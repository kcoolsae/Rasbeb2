/*
 * JDBCContestDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of ContestDao.
 */
public class JDBCContestDao extends JDBCAbstractDao implements ContestDao {

    protected JDBCContestDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public void addContest(ContestType type, Map<String, String> titles, int duration) {
        int userId = getUserId();
        // make contest
        int contestId = insertInto("contests")
                .value("contest_type", type)
                .value("who_created", userId)
                .create();
        // store titles for each language given
        for (Map.Entry<String, String> entry : titles.entrySet()) {
            if (!entry.getValue().isBlank()) {
                insertInto("contests_i18n")
                        .value("contest_id", contestId)
                        .value("lang", entry.getKey())
                        .value("contest_title", entry.getValue())
                        .value("who_created", userId)
                        .execute();
            }
        }
        // set default duration for all supported age groups
        sql("""
                  INSERT INTO contests_ag(contest_id,age_group_id,contest_duration,who_created)
                  SELECT ?,age_group_id,?,?
                    FROM age_groups GROUP BY age_group_id
                """)
                .parameter(contestId)
                .parameter(duration)
                .parameter(userId)
                .execute();
    }

    static Contest makeContest(ResultSet rs) throws SQLException {
        return new Contest(
                rs.getInt("contest_id"),
                ContestType.valueOf(rs.getString("contest_type")),
                ContestStatus.valueOf(rs.getString("contest_status")),
                rs.getString("contest_title")
        );
    }

    public SelectSQLStatement selectContests(String lang) {
        return select("contests.contest_id, contest_type, contest_status, contest_title")
                .from("""
                        contests LEFT JOIN contests_i18n
                        ON contests.contest_id = contests_i18n.contest_id AND lang = ?
                        """)
                .parameter(lang);
    }

    /**
     * Alle contests for the given language and age group, presented
     * in the given language.
     */
    public SelectSQLStatement selectContests(String lang, int ageGroupId) {
        return select("contest_id, contest_type, contest_status, contest_title")
                .from("""
                        contests
                        JOIN contests_ag USING(contest_id)
                        JOIN contests_i18n USING(contest_id)
                        """)
                .where("lang", lang)
                .where("age_group_id", ageGroupId);
    }

    @Override
    public Contest getContest(int contestId, String lang) {
        return selectContests(lang)
                .where("contests.contest_id", contestId)
                .getObject(JDBCContestDao::makeContest);
    }

    private static ContestI18n makeContestI18n(ResultSet rs) throws SQLException {
        return new ContestI18n(
                rs.getInt("contest_id"),
                rs.getString("lang"),
                rs.getString("contest_title")
        );
    }

    @Override
    public List<ContestI18n> getAllContestTranslations(int contestId) {
        return select("contest_id, lang, contest_title")
                .from("contests_i18n")
                .where("contest_id", contestId)
                .orderBy("lang")
                .getList(JDBCContestDao::makeContestI18n);
    }

    @Override
    public ContestFinder findContests(String lang) {
        return new JDBCContestFinder(selectContests(lang));
    }

    @Override
    public void updateContestI18n(int contestId, String lang, String newTitle) {
        update("contests_i18n")
                .set("contest_title", newTitle)
                .set("who_updated", getUserId())
                .where("contest_id", contestId)
                .where("lang", lang)
                .execute();
    }

    @Override
    public void addContestLanguages(int contestId, String languages) {
        if (!languages.isBlank()) {
            for (String lang : languages.split("\\s*[;,]\\s*")) {
                sql("""
                        INSERT INTO contests_i18n(contest_id,lang,contest_title,who_created)
                        VALUES (?,?,'',?)
                        ON CONFLICT (contest_id,lang) DO NOTHING
                        """).parameter(contestId)
                        .parameter(lang.strip())
                        .parameter(getUserId())
                        .execute();
            }
        }
    }

    @Override
    public void removeContestLanguage(int contestId, String lang) {
        deleteFrom("contests_i18n")
                .where("contest_id", contestId)
                .where("lang", lang)
                .execute();
    }

    @Override
    public void changeStatus(int contestId, ContestStatus status) {
        if (status == ContestStatus.CLOSED) {
            // will not result in an action when contest is not official
            call("close_contest(?,?)")
                    .parameter(contestId)
                    .parameter(getUserId())
                    .execute();
        } else {
            update("contests")
                    .set("contest_status", status)
                    .set("who_updated", getUserId())
                    .where("contest_id", contestId)
                    .execute();
        }
    }

    @Override
    public List<Contest> getOrganisableContests(int ageGroupId, String lang) {
        return selectContests(lang, ageGroupId)
                .where("contest_type != 'PUBLIC'")
                .where("(contest_status = 'PUBLISHED' OR contest_status = 'OPEN')")
                .orderBy("contest_id", false)
                .getList(JDBCContestDao::makeContest);
    }

    @Override
    public List<Contest> getViewableContests(int ageGroupId, String lang) {
        return selectContests(lang, ageGroupId)
                .where("(contest_status = 'OPEN' or contest_status = 'CLOSED')")
                .orderBy("contests.contest_id", false)
                .getList(JDBCContestDao::makeContest);
    }

    private static QuestionInSet makeQuestionInSet(ResultSet rs) throws SQLException {
        return new QuestionInSet(
                rs.getInt("question_id"),
                rs.getInt("question_seq_nr"),
                rs.getString("question_title"),
                rs.getInt("question_marks_if_correct"),
                rs.getInt("question_marks_if_incorrect"),
                rs.getString("question_external_id")
        );
    }

    @Override
    public List<QuestionInSet> getQuestionSet(int contestId, int ageGroupId, String lang) {
        return select("question_id, question_seq_nr, question_marks_if_correct, question_marks_if_incorrect, question_title, question_external_id")
                .from("questions_in_set JOIN questions USING(question_id) JOIN questions_i18n USING(question_id)")
                .where("contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .where("lang", lang)
                .orderBy("question_seq_nr")
                .getList(JDBCContestDao::makeQuestionInSet);
    }

    @Override
    public void updateMarks(int contestId, int ageGroupId, List<Integer> ids, List<Integer> marksIfCorrect, List<Integer> marksIfIncorrect) {
        for (int i = 0; i < ids.size(); i++) {
            update("questions_in_set")
                    .set("question_marks_if_correct", marksIfCorrect.get(i))
                    .set("question_marks_if_incorrect", marksIfIncorrect.get(i))
                    .where("question_id", ids.get(i))
                    .where("contest_id", contestId)
                    .where("age_group_id", ageGroupId)
                    .execute();
        }
    }

    @Override
    public void updateOrder(int contestId, int ageGroupId) {
        List<Integer> orderedIds = select("question_id")
                .from("questions_in_set")
                .where("contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .orderBy("question_marks_if_correct")
                .getList(rs -> rs.getInt("question_id"));

        int currentSeqNum = 1;
        // TODO batch or stored procedure? using window functions row_number() OVER?
        for (Integer questionId : orderedIds) {
            update("questions_in_set")
                    .set("question_seq_nr", currentSeqNum)
                    .where("question_id", questionId)
                    .where("contest_id", contestId)
                    .where("age_group_id", ageGroupId)
                    .execute();
            currentSeqNum++;
        }
    }

    @Override
    public void updateOrder(int contestId, int ageGroupId, int seqNum1, int seqNum2) {
        update("questions_in_set")
                .set("question_seq_nr = ? - question_seq_nr", seqNum1 + seqNum2)
                .where("contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .where(String.format("(question_seq_nr = %s OR question_seq_nr = %s)", seqNum1, seqNum2))
                .execute();
    }

    @Override
    public int copyContest(int contestId) {
        return select("copy_contest(?,?)")
                .parameter(contestId)
                .parameter(getUserId())
                .noFrom()
                .getInt();
    }
}
