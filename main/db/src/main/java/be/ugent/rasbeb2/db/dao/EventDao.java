/*
 * EventDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.*;

import java.util.List;

public interface EventDao {

    void addEvent(int contestId, int ageGroupId, int yearId, String title, String lang);

    List<Event> listEvents(int schoolId, int yearId);

    String getEventTitle(int eventId);

    EventHeader getEventHeader (int eventId);

    Event getEvent(int eventId);

    /**
     * Check that the event is for the school of the current user
     */
    boolean isCorrectSchool (int eventId);

    List<Integer> getSelectedPupils(int eventId);

    List<ParticipationWithPupil> getParticipations(int eventId);

    /**
     * Add extra minutes to a participation, granting the student more time to finish
     * the contest.
     */
    void participationAddExtraMinutes(int contestId, int pupilId, int minutesToAdd);

    void reopenParticipation(int contestId, int pupilId);

    void selectPupil(int eventId, int pupilid);

    /**
     * Updates the permissions to an event for a given class. Only the listed
     * pupils are permitted to take part, other pupils of the same class not.
     */
    void updateClassPermissions (int eventId, int classId, Iterable<Integer> selected);

    /**
     * Returns a list of all classes together with their pupils and permissions
     * @param eventId the event for which the permissions are requested
     */
    Iterable<ClassWithPermissions> listClassesWithPermissions(int eventId);

    void editEvent(int eventId, String title);

    /**
     * Opens an event
     */
    void openEvent (int eventId);

    /**
     * Closes an event. For a restricted contest also the results are computed
     * for every related participation
     */
    void closeEvent (int eventId);

    List<PupilWithScore> getPupilsWithScore(int eventId);

    /**
     * Check whether an event has extended status 'open': the event itself is open
     * and the corresponding contest is open
     */
    boolean isOpen(int eventId);
}
