/*
 * QuestionInContestDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.QuestionDao;

import be.ugent.rasbeb2.db.dto.AnswerType;
import be.ugent.rasbeb2.db.dto.QuestionHeader;
import be.ugent.rasbeb2.db.dto.QuestionInContest;
import be.ugent.rasbeb2.db.dto.QuestionWithAgeGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionInContestDaoTest extends TeacherDaoTest {

    private QuestionDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getQuestionDao();
    }

    @Test
    void getQuestionInContest() {
        QuestionInContest actual = dao.getQuestionInContest(2, 3, 1, "fr");
        QuestionInContest expected = new QuestionInContest(
                3, "Question 3 in fr", AnswerType.JSON, "MagicQ3", null, 6, -2
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getQuestionsForContest() {
        List<QuestionHeader> questions = dao.getQuestionsForContest(1, 2, "nl");
        assertThat(questions).extracting(QuestionHeader::title).containsExactly(
                "Question 2 in nl", "Question 3 in nl"
        );
    }

    @Test
    void findQuestionsWithAgeGroups() {
        List<QuestionWithAgeGroups> questions = dao.findQuestionsWithAgeGroups(1, "nl")
                .filter(QuestionWithAgeGroups.Field.EXTERNAL_ID, "2024-XY-0") // only questions 1 and 2
                .getPageOrderedBy(QuestionWithAgeGroups.Field.EXTERNAL_ID, true, 0, 10)
                .getList();
        assertThat(questions).hasSize(2);
        assertThat(questions.getFirst().ageGroups()).containsExactly(1);
        assertThat(questions.get(1).ageGroups()).containsOnly(1, 2); // order not guaranteed
    }

    @Test
    void setQuestionAgeGroups() {
        List<Integer> ageGroups = Arrays.asList(1, null, 3); // List.of does not like nulls
        dao.setQuestionAgeGroups(1, 2, ageGroups);
        assertThat(dao.getQuestionsForContest(1, 1, "nl")).extracting(QuestionHeader::title)
                .containsExactly("Question 1 in nl", "Question 2 in nl", "Question 3 in nl", "Question 4 in nl");
        assertThat(dao.getQuestionsForContest(1, 2, "nl")).extracting(QuestionHeader::title)
                .containsExactly("Question 3 in nl");
        assertThat(dao.getQuestionsForContest(1, 3, "nl")).extracting(QuestionHeader::title)
                .containsOnly("Question 2 in nl", "Question 3 in nl"); // sequence number not defined
    }
}

