/*
 * EventDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.EventDao;
import be.ugent.rasbeb2.db.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class EventDaoTest extends TeacherDaoTest {

    private EventDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getEventDao();
    }

    @Test
    void getEvent() {
        assertThat (dao.getEventTitle(5)).isEqualTo("Event 5");

        EventHeader expectedHeader = new EventHeader(
                5, "Event 5", "Contest 1 in en", "en", "Age group 3 in en"
        );
        assertThat (dao.getEventHeader(5)).isEqualTo(expectedHeader);
        Event expectedEvent = new Event(
             expectedHeader, 1, EventStatus.OPEN, ContestStatus.PUBLISHED, ContestType.OFFICIAL
        );
        assertThat (dao.getEvent(5)).isEqualTo(expectedEvent);
    }

    @Test
    void listEvents() {
        List<Event> events = dao.listEvents(24);
        assertThat(events).extracting(Event::id)
                .containsExactly(1, 2, 3, 4, 7);
        assertThat(events).extracting(ev -> ev.header().title())
                .containsExactly("Event 1", "Event 2", "Event 3", "Event 4", "Event 7");
    }

    @Test
    void isCorrectSchool() {
        assertThat(dao.isCorrectSchool(5)).isTrue();
        assertThat(dao.isCorrectSchool(6)).isFalse();
    }

    @Test
    void editEvent() {
        dao.editEvent(1, "New title");
        assertThat(dao.getEventTitle(1)).isEqualTo("New title");
    }

    @Test
    void openEvent() {
        dao.openEvent(2);
        assertThat(dao.getEvent(2).eventStatus()).isEqualTo(EventStatus.OPEN);
    }

    @Test
    void isOpen() {
        assertThat(dao.isOpen(2)).isFalse(); // event and contest not open
        assertThat(dao.isOpen(1)).isFalse(); // contest not open
        assertThat(dao.isOpen(7)).isFalse(); // event not open
        dao.openEvent(7);
        assertThat(dao.isOpen(7)).isTrue();
    }

    @Test
    void getSelectedPupils() {
        assertThat(dao.getSelectedPupils(1)).containsExactly(1, 2);
    }

    @Test
    void selectPupil() {
        dao.selectPupil(1, 3);
        assertThat(dao.getSelectedPupils(1)).containsExactly(1, 2, 3);
    }

    @Test
    void listClassesWithPermissions() {
        Iterable<ClassWithPermissions> cwp = dao.listClassesWithPermissions(2);
        // classes of this school are 1,2,3,4 but only 3,4 are of the year of the event
        // pupils are 1,2 and 5,6,8 respectively - only 5 is selected for the event
        assertThat(cwp).extracting(c -> c.group().id())
                .containsExactly(3,4);
        Iterator<ClassWithPermissions> iterator = cwp.iterator();
        ClassWithPermissions first = iterator.next();
        assertThat (first.pupils()).containsExactly(
                new PupilWithPermission(1, "Pupil 1", false),
                new PupilWithPermission(2, "Pupil 2", false)
        );
        ClassWithPermissions second = iterator.next();
        assertThat (second.pupils()).containsExactly(
                new PupilWithPermission(5, "Pupil 5", true),
                new PupilWithPermission(6, "Pupil 6", false),
                new PupilWithPermission(8, "Pupil 8", false)
        );
    }

    @Test
    void updateClassPermissions() {
        dao.updateClassPermissions(2, 4, List.of(6)); // deselects 5
        assertThat(dao.getSelectedPupils(2)).containsExactly(3,6);
    }

    @Test
    void getParticipations() {
        List<ParticipationWithPupil> actual = dao.getParticipations(1);
        List<ParticipationWithPupil> expected = List.of(
                new ParticipationWithPupil(1, 1, "Pupil 1", false, actual.get(0).deadline(), 0, 36),
                new ParticipationWithPupil(2, 1, "Pupil 2", true, actual.get(1).deadline(), 0, 36)
        );
        assertThat(actual).isEqualTo(expected);
        // 6 did not participate in event 3
        assertThat(dao.getParticipations(3)).flatExtracting(
                ParticipationWithPupil::pupilId, ParticipationWithPupil::maxMarks)
                .containsExactly(6,0);
    }

    @Test
    void participationAddExtraMinutes() {
        // minutes of pupil 1 in contest 1
        Instant expected = dao.getParticipations(1).getFirst().deadline().plus(10, java.time.temporal.ChronoUnit.MINUTES);
        dao.participationAddExtraMinutes(1, 1, 10);
        Instant actual = dao.getParticipations(1).getFirst().deadline();
        assertThat(actual).isCloseTo(expected, within(2, java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void reopenParticipation () {
        assertThat(dao.getParticipations(1).get(1).closed()).isTrue();
        dao.reopenParticipation(1, 2);
        assertThat(dao.getParticipations(1).get(1).closed()).isFalse();
    }

    @Test
    void getPupilsWithScore() {
        dao.closeEvent(7); // restricted contest 3:2, questions 1,2,3,4 max marks 6,6,6,6 + offset 8
        List<PupilWithScore> actual = dao.getPupilsWithScore(7);
        List<PupilWithScore> expected = List.of(
                new PupilWithScore(5, "Pupil 5", "3b", 12, 32, false), // answered wrong/correct/blank/blank
                new PupilWithScore(6, "Pupil 6", "3b", 0, 0, false)
        );
        assertThat(actual).isEqualTo(expected);
    }

}
