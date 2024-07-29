/*
 * ParticipationDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.Participation;
import be.ugent.rasbeb2.db.dto.ParticipationWithMarks;
import be.ugent.rasbeb2.db.dto.QuestionWithFeedback;

import java.util.List;

public interface ParticipationDao {

    /**
     * Create participation for the current anonymous user for contest and age group without an event (i.e. participations for anonymous users).
     */
    void createParticipation(int contestId, int ageGroupId, String lang, int pupilId);

    /**
     * Create participation for the current user for an existing event. Returns the contest id
     */
    int createParticipation(int eventId);

    /**
     * Return the participation record of the current user for the given contest.
     */
    Participation getParticipation(int contestId);

    String getAnswer (int contestId, int pupilId, int questionId);

    void updateAnswer(int contestId, int pupilId, int questionId, String answer);

    /**
     * Closes the participation for the given contest and the current user.
     * Marks are not computed.
     */
    void closeParticipation(int contestId);

    /**
     * Closes the participation for the given contest and the current anonymous user.
     * Computes the marks for this participation.
     */
    void closeParticipationAndComputeMarks(int contestId);

    /**
     * Get the total marks for the given contest and the current user.
     */
    ParticipationWithMarks getMarks(int contestId);

    /**
     * Retrieve question and feedback information for the current user and given question
     */
    QuestionWithFeedback getQuestionWithFeedback(int contestId, int questionId, int ageGroupId, String lang);

    /**
     * Retrieve question and feedback information for the current user and the given contest
     */
    List<QuestionWithFeedback> listQuestionsWithFeedback(int contestId, int ageGroupId, String lang);

}
