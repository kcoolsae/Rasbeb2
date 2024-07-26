/*
 * ContestDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.*;

import java.util.List;
import java.util.Map;

public interface ContestDao {

    /**
     * Adds a contest of the given type with the given titles (as language/string pairs).
     * Also adds *all* age groups to this contest, with the given duration.
     */
    void addContest(ContestType type, Map<String,String> titles, int duration);

    /**
     * Return contest information, including a title, by preference in the requested language
     */
    Contest getContest(int contestId, String lang);

    List<ContestI18n> getAllContestTranslations(int contestId);

    interface ContestFinder extends Finder<Contest, Contest.Field, ContestDao.ContestFinder> {}
    ContestFinder findContests (String lang);

    void updateContestI18n(int contestId, String lang, String newTitle);


    /**
     * Adds languages to the given contest with blank titles. Takes a comma separated list of language codes
     */
    void addContestLanguages(int contestId, String languages);

    /**
     * Remove contest language
     */
    void removeContestLanguage(int contestId, String lang);


    /**
     * Change the status of the contest.
     * <p>
     * In the special case where
     * the contest if official and the new status is closed, also all events
     * and participations corresponding to this contest are closed
     */
    void changeStatus(int contestId, ContestStatus status);

    /**
     * Returns a list of all restricted or official contests that
     * are currently available for events in the given language.
     * Most recent first (according to contest id).
     */
    List<Contest> getOrganisableContests(int ageGroupId, String lang);

    /**
     * Returns a list of all contests that can be viewed by a teacher
     * in the given language.
     * Most recent first (according to contest id).
     */
    List<Contest> getViewableContests(int ageGroupId, String lang);

    List<QuestionInSet> getQuestionSet(int contestId, int ageGroupId, String lang);

    void updateMarks(int contestId, int ageGroupId, List<Integer> ids, List<Integer> marksIfCorrect, List<Integer> marksIfIncorrect);

    /**
     * Update the order of the questions in the given contest to reflect the difficulty of the questions
     */
    void updateOrder(int contestId, int ageGroupId);

    /**
     * Interchange the order of the questions with the given sequence numbers in the given contest
     */
    void updateOrder(int contestId, int ageGroupId, int seqNum1, int seqNum2);

    /**
     * Make a copy of the given contest.
     * @return the id of the new contest
     */
    int copyContest(int contestId);

}
