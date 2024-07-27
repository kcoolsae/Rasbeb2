/*
 * QuestionDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;


import be.ugent.rasbeb2.db.dto.*;

import java.util.List;

public interface QuestionDao {

    /**
     * Create a new question. Magic numbers are generated automatically.
     */
    int createQuestion(AnswerType type, String typeExtra, String externalId);

    void editQuestion(int questionId, AnswerType type, String typeExtra, String externalId);

    Question getQuestion(int questionId, String lang);

    interface QuestionFinder extends Finder<QuestionHeader, QuestionHeader.Field, QuestionDao.QuestionFinder> {}
    QuestionDao.QuestionFinder findQuestions (String lang);

    boolean questionExists(String externalId);

    /**
     * Check whether either the question or the feedback for the given language has already been uploaded
     */
    boolean questionAlreadyUploaded (int questionId, String lang);

    enum FileType {
        QUESTION, FEEDBACK
    }

    void setUploaded (int questionId, String lang, FileType fileType);

    String getMagic (int questionId, FileType fileType);
    /**
     * Add an empty translation for the given language
     */
    void addTranslation(int questionId, String lang);

    void addTranslation(int questionId, Translation translation);

    void editTranslation(int questionId, String lang, String newTitle, String newSolution);

    void removeTranslation(int questionId, String lang);

    List<QuestionI18n> getQuestionI18n(int questionId);

    /**
     * Set the age groups for the given question in the given contest, removing all age groups
     * that are not mentioned in the list
     * Nulls in the list are ignored
     */
    void setQuestionAgeGroups(int contestId, int questionId, List<Integer> ageGroups);

    interface QuestionWithAgeGroupsFinder extends Finder<QuestionWithAgeGroups, QuestionWithAgeGroups.Field, QuestionDao.QuestionWithAgeGroupsFinder> {}
    QuestionDao.QuestionWithAgeGroupsFinder findQuestionsWithAgeGroups (int contestId, String lang);

    QuestionInContest getQuestionInContest(int contestId, int questionId, int ageGroupId, String lang);

    List<QuestionHeader> getQuestionsForContest(int contestId, int ageGroupId, String lang);

}
