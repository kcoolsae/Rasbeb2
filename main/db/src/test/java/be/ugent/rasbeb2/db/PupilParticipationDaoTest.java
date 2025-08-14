/*
 * PupilParticipationDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dto.AnswerType;
import be.ugent.rasbeb2.db.dto.Participation;
import be.ugent.rasbeb2.db.dto.ParticipationWithMarks;
import be.ugent.rasbeb2.db.dto.QuestionWithFeedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PupilParticipationDaoTest extends PupilDaoTest {

    private ParticipationDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getParticipationDao();
    }

    @Test
    void getParticipation() {
        Participation actual = dao.getParticipation(3);
        assertThat (actual).extracting(
                Participation::contestId, Participation::pupilId,
                Participation::ageGroupId, Participation::eventId)
                .containsExactly(3, 5, 2, 7);
        assertThat (actual.lang()).isEqualTo("en");
    }

    @Test
    void createParticipation() {
        // pupil 5 is registered for events/contests 2/1 and 7/3
        int contestId = dao.createParticipation(2);
        assertThat(contestId).isEqualTo(1);

        Participation actual = dao.getParticipation(contestId);
        Participation expected = new Participation(1, 5, 2, "nl", 2, false,
                actual.timeLeftInSeconds(), 40*60, actual.deadline()
        );
        assertThat(actual).isEqualTo(expected);
        // timing cannot easily be tested
        assertThat(actual.timeLeftInSeconds()).isCloseTo(40*60, within(2));
        assertThat(actual.deadline()).isCloseTo(
                Instant.now().plus(40, java.time.temporal.ChronoUnit.MINUTES),
                within(2, java.time.temporal.ChronoUnit.SECONDS)
        );
    }


    @Test
    void getAnswer () {
        assertThat (dao.getAnswerAndModel(3, 5, 1)).containsExactly("A", null);
        assertThat (dao.getAnswerAndModel(3, 5, 3)).isNull();
    }

    @Test
    void updateAnswerAndModel() {
        dao.updateAnswerAndModel(3, 5, 1, "B", "model");
        assertThat (dao.getAnswerAndModel(3, 5, 1)).containsExactly("B", "model");
        dao.updateAnswerAndModel(3, 5, 1, null, null);
        assertThat (dao.getAnswerAndModel(3, 5, 1)).containsExactly(null, null);
        dao.updateAnswerAndModel(3, 5, 3, "Answer", "MODEL");
        assertThat (dao.getAnswerAndModel(3, 5, 3)).containsExactly("Answer", "MODEL");
    }

    @Test
    void closeParticipation() {
        dao.closeParticipation(3);
        assertThat (dao.getParticipation(3).closed()).isTrue();
    }

    @Test
    void closeParticipationAndComputeMarks() {
        // must really be used only for anonymous users, but should work anyway
        dao.closeParticipationAndComputeMarks(3);
        assertThat (dao.getParticipation(3).closed()).isTrue();
        // question 1 incorrect, 2 correct, 3 blank, marks 6 -2 + 0 = 4 + offset 6
        ParticipationWithMarks actual = dao.getMarks(3);
        ParticipationWithMarks expected = new ParticipationWithMarks(
                3, 5, 2, "en", 12, 32
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getQuestionWithFeedback() {
        dao.closeParticipationAndComputeMarks(3);
        QuestionWithFeedback actual = dao.getQuestionWithFeedback(3, 1, 2, "en");
        QuestionWithFeedback expected = new QuestionWithFeedback(
                1, AnswerType.MC, "Question 1 in en", "MagicQ1", "MagicF1", "A", null, -2,
                6);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void listQuestionsWithFeedback() {
        dao.closeParticipationAndComputeMarks(3);
        assertThat(dao.listQuestionsWithFeedback(3, 2, "en"))
                .hasSize(4)
                .extracting(QuestionWithFeedback::marks)
                .containsExactly(-2, 6, 0, 0);
    }

}
