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
     * Create participation for contest and age group without an event (i.e. participations for anonymous users).
     */
    void create(int contestId, int ageGroupId, String lang, int pupilId);

    /**
     * Create participation for an existing event (i.e. participations for pupils). Returns the contest id
     */
    int create(int eventId, int pupilId);

    Participation get(int contestId, int pupilId);

    String getAnswer (int contestId, int pupilId, int questionId);

    void updateAnswer(int contestId, int pupilId, int questionId, String answer);

    void close(int contestId, int pupilId);

    void closeAndComputeMarks(int contestId, int pupilId);

    ParticipationWithMarks getMarks(int contestId, int pupilId);

    List<QuestionWithFeedback> listQuestionsWithFeedback(int contestId, int pupilId, int ageGroupId, String lang);

    QuestionWithFeedback getQuestionWithFeedback(int contestId, int questionId, int pupilId, int ageGroupId, String lang);

}
