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

    /**
     * List the events for the school of the current user in the given year
     */
    List<Event> listEvents(int yearId);

    String getEventTitle(int eventId);

    EventHeader getEventHeader (int eventId);

    Event getEvent(int eventId);

    void editEvent(int eventId, String title);

    /**
     * Opens an event
     */
    void openEvent (int eventId);

    /**
     * Check whether an event has extended status 'open': the event itself is open
     * and the corresponding contest is open
     */
    boolean isOpen(int eventId);

    /**
     * Closes an event. For a restricted contest also the results are computed
     * for every related participation
     */
    void closeEvent (int eventId);

    /**
     * Check that the event is for the school of the current user
     */
    boolean isCorrectSchool (int eventId);

    List<Integer> getSelectedPupils(int eventId);

    void selectPupil(int eventId, int pupilid);

    /**
     * Returns a list of all classes of the current school,
     * together with all their pupils and permissions. Output is restricted
     * to classes for the year of the event
     * @param eventId the event for which the permissions are requested
     */
    Iterable<ClassWithPermissions> listClassesWithPermissions(int eventId);

    /**
     * Updates the permissions to an event for a given class. Only the listed
     * pupils are permitted to take part, other pupils of the same class not.
     */
    void updateClassPermissions (int eventId, int classId, Iterable<Integer> selected);

    List<ParticipationWithPupil> getParticipations(int eventId);

    /**
     * Add extra minutes to a participation, granting the student more time to finish
     * the contest.
     */
    void participationAddExtraMinutes(int contestId, int pupilId, int minutesToAdd);

    void reopenParticipation(int contestId, int pupilId);

    List<PupilWithScore> getPupilsWithScore(int eventId);

    /**
     * Get pupils with their scores for all events of the current school,
     * the given contest in the given year
     * but only those for which the participation is already closed.
     */
    List<PupilWithScore> getParticipatingPupils(int contestId, int year);

    /**
     * Toggle participation visibility, but only for participations that are already closed
     */
    void toggleParticipationVisibility(int contestId, int participationId);
}
