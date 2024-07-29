/*
 * PupilContestDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.PupilContestDao;
import be.ugent.rasbeb2.db.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PupilContestDaoTest extends PupilDaoTest {

    private PupilContestDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getPupilContestDao();
    }

    @Test
    void getContests() {
        List<ContestForPupilTable> contests = dao.getContests();
        // pupil 5 is registered for events/contests 2/1 and 7/2
        List<Event> events = contests.stream().map(ContestForPupilTable::event).toList();
        assertThat(events).flatExtracting(Event::id, Event::contestId)
                .containsExactly(2, 1, 7, 3);
        assertThat(contests).extracting(ContestForPupilTable::participationClosed)
                .containsExactly(false, false);
        assertThat(contests).extracting(ContestForPupilTable::participationDeadline)
                .containsExactly(null, null);
        // TODO also test contests where participated
    }

    @Test
    void getOpenPublicContests() {
        assertThat(dao.getOpenPublicContests("nl"))
                .extracting(ContestForAnonTable::contestTitle)
                .containsExactly("Contest 5 in nl");
        assertThat(dao.getOpenPublicContests("nl"))
                .extracting(ContestForAnonTable::durations)
                .containsExactly(Map.of(1,50,3,30));
    }

    @Test
    void getContestWithAgeGroup() {
        Contest expectedContest = new Contest(1, ContestType.OFFICIAL, ContestStatus.PUBLISHED, "Contest 1 in nl");
        ContestWithAgeGroup expected = new ContestWithAgeGroup(
                expectedContest,
                new AgeGroupWithDuration(
                        new AgeGroup(2,
                                "Age group 2 in nl",
                                "Description of age group 2 in nl"),
                40)
        );
        assertThat(dao.getContestWithAgeGroup(1, 2, "nl")).isEqualTo(expected);
    }

}
