/*
 * ContestDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContestDaoTest extends OrganiserDaoTest {

    private ContestDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getContestDao();
    }

    @Test
    void getContest() {
        assertThat(dao.getContest(5, "en").title()).isEqualTo("Contest 5 in en");
    }

    @Test
    void getAllContestTranslations() {
        List<ContestI18n> contests = dao.getAllContestTranslations(4);
        assertThat(contests).extracting(ContestI18n::title)
                .containsExactly(
                        "Contest 4 in en", "Contest 4 in nl"
                );
    }

    @Test
    void findContests() {
        List<Contest> contests = dao.findContests("nl")
                .filter(Contest.Field.TITLE, "test") // currently filtering on enums is not supported
                .getPageOrderedBy(Contest.Field.STATUS, false, 0, 10)
                .getList();
        assertThat(contests).extracting(Contest::id)
                .containsExactly(3, 5, 1, 4, 2, 6); // Hm. order is not really predictable...
    }

    @Test
    void addContest() {
        dao.addContest(
                ContestType.OFFICIAL,
                Map.of("en", "New contest", "de", "Neuer Wettbewerb"),
                60);
        // find id of contest
        int id = dao.findContests("en")
                .filter(Contest.Field.TITLE, "New contest")
                .getPageOrderedBy(Contest.Field.STATUS, false, 0, 10)
                .getList().getFirst().id();

        Contest expected = new Contest(id, ContestType.OFFICIAL, ContestStatus.PENDING, "New contest");

        assertThat(dao.getContest(id, "en")).isEqualTo(expected);
    }

    @Test
    void updateContestI18n() {
        dao.updateContestI18n(5, "en", "New title");
        assertThat(dao.getContest(5, "en").title()).isEqualTo("New title");
    }

    @Test
    void addContestLanguages() {
        dao.addContestLanguages(4, " de, nl, es");
        List<ContestI18n> contests = dao.getAllContestTranslations(4);
        assertThat(contests).flatExtracting(ContestI18n::lang, ContestI18n::title)
                .containsExactly(
                        "de", "", "en", "Contest 4 in en",
                        "es", "", "nl", "Contest 4 in nl"
                );
    }

    @Test
    void removeContestLanguage() {
        dao.removeContestLanguage(4, "nl");
        List<ContestI18n> contests = dao.getAllContestTranslations(4);
        assertThat(contests).extracting(ContestI18n::lang)
                .containsExactly("en");
    }

    @Test
    void changeStatus() {
        // note that only official contests can be closed
        dao.changeStatus(5, ContestStatus.PENDING);
        assertThat(dao.getContest(5, "en").status()).isEqualTo(ContestStatus.PENDING);
    }

    @Test
    void getOrganisableContests() {
        // must not be pending or closed or public (1, 3, 4)
        // and exist in the given language and age group
        List<Contest> contests = dao.getOrganisableContests(1, "en");
        assertThat(contests).extracting(Contest::id)
                .containsExactly(4, 1); // 3 does not organise age group 1

        contests = dao.getOrganisableContests(2, "nl");
        assertThat(contests).extracting(Contest::id)
                .containsExactly(3, 1); // 4 does not organise age group 2

        contests = dao.getOrganisableContests(1, "fr");
        assertThat(contests).extracting(Contest::id)
                .containsExactly(1); // 4 does not organise in french
    }

    @Test
    void getViewableContests() {
        // must be open. For official it must be closed (3,5)
        // and exist in the given language and age group
        List<Contest> contests = dao.getViewableContests(1, "en");
        assertThat(contests).extracting(Contest::id)
                .containsExactly(5); // 3 does not organise age group 1

        contests = dao.getViewableContests(2, "nl");
        assertThat(contests).extracting(Contest::id)
                .containsExactly(3); // 5 does not organise age group 2

        contests = dao.getViewableContests(1, "de");
        assertThat(contests).extracting(Contest::id)
                .isEmpty();
    }

    @Test
    void getQuestionSet() {
        List<QuestionInSet> questions = dao.getQuestionSet(1, 2, "nl");
        assertThat(questions).extracting(QuestionInSet::title).containsExactly(
                "Question 2 in nl", "Question 3 in nl"
        );
        assertThat(questions).extracting(QuestionInSet::marksIfCorrect).containsExactly(6, 9);
        assertThat(questions).extracting(QuestionInSet::marksIfIncorrect).containsExactly(-2, -3);

    }

    @Test
    void updateMarks() {
        dao.updateMarks(1, 2, List.of(3, 2, 1), List.of(7, 8, 9), List.of(-1, -11, -13));
        List<QuestionInSet> questions = dao.getQuestionSet(1, 2, "fr");
        assertThat(questions).extracting(QuestionInSet::marksIfCorrect).containsExactly(8, 7);
        assertThat(questions).extracting(QuestionInSet::marksIfIncorrect).containsExactly(-11, -1);
    }

    @Test
    void updateOrder2() {
        // get sequence numbers of the questions
        List<QuestionInSet> questions = dao.getQuestionSet(2, 1, "nl");
        int seqNum1 = questions.get(0).seqNum();
        int seqNum2 = questions.get(1).seqNum();
        // interchange them
        dao.updateOrder(2, 1, seqNum1, seqNum2); // interchange first two questions
        questions = dao.getQuestionSet(2, 1, "nl");
        assertThat(questions).extracting(QuestionInSet::title)
                .containsExactly("Question 2 in nl", "Question 1 in nl", "Question 3 in nl");
    }

    @Test
    void updateOrder() {
        // make first question more difficult, third easier
        dao.updateMarks(2, 1, List.of(1,3), List.of(8,4), List.of(-4,0));
        // reorder them according to difficulty
        dao.updateOrder(2, 1);
        List<QuestionInSet>  questions = dao.getQuestionSet(2, 1, "nl");
        assertThat(questions).extracting(QuestionInSet::title)
                .containsExactly("Question 3 in nl", "Question 2 in nl", "Question 1 in nl");
    }

    @Test
    void getContestLanguages() {
        assertThat (dao.getContestLanguages(1)).containsExactly("en", "fr", "nl");
        assertThat (dao.getContestLanguages(4)).containsExactly("en", "nl");
    }

    // method copyContest is tested in class CopyContestTes
    @Test
    void listQuestionLinks() {
        List<ContestDao.QuestionLink> links = dao.getQuestionLinks(1, "en");
        assertThat(links).extracting(ContestDao.QuestionLink::magicQ).containsExactly(
                null, "MagicQ2", null
        );
        assertThat(links).extracting(ContestDao.QuestionLink::magicF).containsExactly(
                null, "MagicF2", "MagicF3"
        );
        assertThat(links).extracting(ContestDao.QuestionLink::externalId).containsExactly(
                "2024-XY-01", "2024-XY-02", "2024-XY-12"
        );
        assertThat(links).extracting(ContestDao.QuestionLink::title).containsExactly(
                "Question 1 in en", "Question 2 in en", "Question 3 in en"
        );
    }
}
