/*
 * CopyContestTest.java
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

import static org.assertj.core.api.Assertions.assertThat;

class CopyContestTest extends OrganiserDaoTest {

    private ContestDao dao;

    private int contestId;

    @BeforeEach
    void setupDao() {
        dao = dac.getContestDao();
        contestId = dao.copyContest(1);
    }

    @Test
    void contest() {
        Contest expected = new Contest(
                contestId, ContestType.RESTRICTED, ContestStatus.OPEN,
                "Contest 1 in en (+)"
        );
        assertThat (dao.getContest(contestId, "en")).isEqualTo(expected);
    }

    @Test
    void contestI18n() {
        List<ContestI18n> contests = dao.getAllContestTranslations(contestId);
        assertThat(contests) .flatExtracting(ContestI18n::lang, ContestI18n::title)
                .containsExactly(
                        "en", "Contest 1 in en (+)",
                        "fr", "Contest 1 in fr (+)",
                        "nl", "Contest 1 in nl (+)"
                );
    }

    @Test
    void contestAG() {
        List<AgeGroup> ageGroups = dac.getAgeGroupDao().getAgeGroups(contestId, "nl");
        assertThat(ageGroups).extracting(AgeGroup::id)
                .containsExactly(1, 2, 3);
    }

    @Test
    void questions() {
        List<QuestionHeader> questions = dac.getQuestionDao().getQuestionsForContest(contestId, 2, "nl");
        assertThat(questions).extracting(QuestionHeader::title).containsExactly(
                "Question 2 in nl", "Question 3 in nl"
        );
    }
}
