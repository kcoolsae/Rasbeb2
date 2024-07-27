/*
 * QuestionDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.QuestionDao;

import be.ugent.rasbeb2.db.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionDaoTest extends OrganiserDaoTest {

    private QuestionDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getQuestionDao();
    }

    @Test
    void getQuestion() {
        Question expected = new Question(
                new QuestionHeader(2, AnswerType.INT, "Question 2 in en", "2024-XY-02"),
                null, "MagicQ2", "MagicF2"
        );
        Question actual = dao.getQuestion(2, "en");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findQuestions() {
        List<QuestionHeader> questions = dao.findQuestions("nl").filter(QuestionHeader.Field.ID, "2024-XY-0")
                .getPageOrderedBy(QuestionHeader.Field.TITLE, true, 0, 10)
                .getList();
        assertThat(questions).extracting(QuestionHeader::title)
                .containsExactly("Question 1 in nl", "Question 2 in nl");
    }

    @Test
    void createQuestion() {
        int questionId = dao.createQuestion(AnswerType.INT, "extra", "2024-XY-03");
        dao.addTranslation(questionId, new Translation("en", "Title", "Answer"));
        Question actual = dao.getQuestion(questionId, "en");
        assertThat(actual.header()).isEqualTo(
                new QuestionHeader(questionId, AnswerType.INT, "Title", "2024-XY-03")
        );
        assertThat(actual.typeExtra()).isEqualTo("extra");
        assertThat(actual.magicQ()).isNotNull();
        assertThat(actual.magicF()).isNotNull();
    }

    @Test
    void editQuestion() {
        dao.editQuestion(2, AnswerType.MC, "5", "2024-AL-02");
        Question actual = dao.getQuestion(2, "en");
        assertThat(actual.header().answerType()).isEqualTo(AnswerType.MC);
        assertThat(actual.header().externalId()).isEqualTo("2024-AL-02");
        assertThat(actual.typeExtra()).isEqualTo("5");
    }

    @Test
    void getQuestionI18n() {
        List<QuestionI18n> questions = dao.getQuestionI18n(1);
        assertThat(questions).flatExtracting(QuestionI18n::lang, QuestionI18n::title, QuestionI18n::solution)
                .containsExactly(
                        "en", "Question 1 in en", "Answer to 1 in en",
                        "fr", "Question 1 in fr", "Answer to 1 in fr",
                        "nl", "Question 1 in nl", "Answer to 1 in nl"
                );
    }

    @Test
    void editTranslation() {
        dao.editTranslation(2, "fr", "New title", "New solution");
        QuestionI18n actual = dao.getQuestionI18n(2).get(1); // fr
        QuestionI18n expected = new QuestionI18n(2, "fr", "New title", "New solution", false, false);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addTranslation() {
        dao.addTranslation(3, "de");
        QuestionI18n actual = dao.getQuestionI18n(3).get(0);
        QuestionI18n expected = new QuestionI18n(3, "de", "", "", false, false);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addTranslation2() {
        dao.addTranslation(3, new Translation("de", "Title", "Answer"));
        QuestionI18n actual = dao.getQuestionI18n(3).get(0);
        QuestionI18n expected = new QuestionI18n(3, "de", "Title", "Answer", false, false);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void removeTranslation() {
        dao.removeTranslation(3, "fr");
        List<QuestionI18n> actual = dao.getQuestionI18n(3);
        assertThat(actual).extracting(QuestionI18n::lang)
                .containsExactly("en", "nl");
    }

    @Test
    void questionExists() {
        assertThat(dao.questionExists("2024-XY-01")).isTrue();
        assertThat(dao.questionExists("2024-XY-03")).isFalse();
    }

    @Test
    void questionAlreadyUploaded() {
        assertThat(dao.questionAlreadyUploaded(1, "en")).isFalse();
        dao.setUploaded(1, "en", QuestionDao.FileType.QUESTION);
        assertThat(dao.questionAlreadyUploaded(1, "en")).isTrue();
        QuestionI18n expected = new QuestionI18n(1, "en", "Question 1 in en", "Answer to 1 in en", true, false);
        QuestionI18n actual = dao.getQuestionI18n(1).getFirst(); // en
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void setUploaded() {
        dao.setUploaded(2, "fr", QuestionDao.FileType.FEEDBACK);
        assertThat(dao.questionAlreadyUploaded(2, "fr")).isTrue();
        QuestionI18n expected = new QuestionI18n(2, "fr", "Question 2 in fr", "Answer to 2 in fr", false, true);
        QuestionI18n actual = dao.getQuestionI18n(2).get(1); // fr
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getMagic() {
        assertThat(dao.getMagic(1, QuestionDao.FileType.QUESTION)).isEqualTo("MagicQ1");
        assertThat(dao.getMagic(3, QuestionDao.FileType.FEEDBACK)).isEqualTo("MagicF3");
    }


}
