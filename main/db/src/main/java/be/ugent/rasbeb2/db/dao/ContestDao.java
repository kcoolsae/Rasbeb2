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

    ContestWithAgeGroup getContestWithAgeGroup(int contestId, int ageGroupId, String lang);

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

    List<AgeGroup> getAllAgeGroups(String lang);

    /**
     * Return the list of age groups for which the contest is organised.
     * @param lang Language to be used for the age group titles
     */
    List<AgeGroup> getAgeGroups(int contestId, String lang);

    /**
     * Return the list of all age groups with duration for which the contest is organised. Duration
     * can be null if the contest is not organised for this age group.
     * @param lang Language to be used for the age group titles
     */
    List<AgeGroupWithDuration> getAgeGroupsWithDuration(int contestId, String lang);

    void removeAgeGroup(int contestId, int ageGroupId);

    void updateDuration(int contestId, int ageGroupId, int duration);

    void changeStatus(int contestId, ContestStatus status);

    /**
     * Returns a list of all restricted or official contests that
     * are currently available for events in the given language
     */
    List<Contest> getOrganisableContests(int ageGroupId, String lang);

    /**
     * Returns a list of all contests that can be viewed by a teacher in the given language
     */
    List<Contest> getViewableContests(int ageGroupId, String lang);

    /**
     * Returns a list of all contests that can be taken by an anonymous user
     */
    List<ContestForAnonTable> getOpenPublicContests(String lang);

    QuestionWithAnswer getQuestionWithAnswer(int questionId, int pupilId);

    List<QuestionInSet> getQuestionSet(int contestId, int ageGroupId, String lang);

    int getFirstQuestionIdInSet(int contestId, int ageGroupId);

    void updateMarks(int contestId, int ageGroupId, List<Integer> ids, List<Integer> marksIfCorrect, List<Integer> marksIfIncorrect);

    /**
     * Update the order of the questions in the given contest to reflect the difficulty of the questions
     */
    void updateOrder(int contestId, int ageGroupId);

    /**
     * Interchange the order of the questions with the given sequence numbers in the given contest
     */
    void updateOrder(int contestId, int ageGroupId, int seqNum1, int seqNum2);

}
