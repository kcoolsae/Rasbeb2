/*
 * CloseOfficialTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.EventDao;
import be.ugent.rasbeb2.db.dto.Contest;
import be.ugent.rasbeb2.db.dto.ContestStatus;
import be.ugent.rasbeb2.db.dto.Event;
import be.ugent.rasbeb2.db.dto.ParticipationWithPupil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CloseOfficialTest extends TeacherDaoTest{

    @BeforeEach
    void setup () {
        dac.getContestDao().changeStatus(1, ContestStatus.CLOSED);
    }

    @Test
    void statusChanged() {
        // contest 1 is now closed
        assertThat (dac.getContestDao().getContest(1, "en").status()).isEqualTo(ContestStatus.CLOSED);
    }

    @Test
    void eventsClosed() {
        // 1-4 should be closed, event 7 is another contest, should remain open
        assertThat (dac.getEventDao().listEvents(24)).extracting(Event::contestStatus)
                .containsExactly(ContestStatus.CLOSED, ContestStatus.CLOSED, ContestStatus.CLOSED, ContestStatus.CLOSED, ContestStatus.OPEN);
    }

    @Test
    void participationsClosed() {
        EventDao dao = dac.getEventDao();
        List<ParticipationWithPupil> actual = dao.getParticipations(1);
        List<ParticipationWithPupil> expected = List.of(
                new ParticipationWithPupil(1, 1, "Pupil 1", true, actual.get(0).deadline(), 20, 36),
                new ParticipationWithPupil(2, 1, "Pupil 2", true, actual.get(1).deadline(), 30, 36)
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void isViewable() {
        List<Contest> contests = dac.getContestDao().getViewableContests(1, "en");
        assertThat(contests).extracting(Contest::id).contains(1);
    }

    @Test
    void isNotOrganisable() {
        List<Contest> contests = dac.getContestDao().getOrganisableContests(1, "en");
        assertThat(contests).extracting(Contest::id).doesNotContain(1);
    }
}
