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

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        // pupils are 1,2 and 5,6 respectively - only 5 is selected for the event
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
                new PupilWithPermission(6, "Pupil 6", false)
        );
    }

    @Test
    void updateClassPermissions() {
        dao.updateClassPermissions(2, 4, List.of(6)); // deselects 5
        assertThat(dao.getSelectedPupils(2)).containsExactly(3,6);
    }

    @Test
    void getParticipations() {
        // TODO
    }

    @Test
    void participationAddExtraMinutes() {
        // TODO
    }

    @Test
    void reopenParticipation () {
        // TODO
    }

    @Test
    void getPupilsWithScore() {
        // TODO
    }

}
