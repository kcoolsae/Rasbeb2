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

    interface QuestionFinder extends Finder<QuestionHeader, QuestionHeader.Field, QuestionDao.QuestionFinder> {}
    QuestionDao.QuestionFinder findQuestions (String lang);

    interface QuestionWithAgeGroupsFinder extends Finder<QuestionWithAgeGroups, QuestionWithAgeGroups.Field, QuestionDao.QuestionWithAgeGroupsFinder> {}
    QuestionDao.QuestionWithAgeGroupsFinder findQuestionsWithAgeGroups (int contestId, String lang);

    int createQuestion(AnswerType type, String typeExtra, String externalId);

    Question getQuestion(int questionId, String lang);

    QuestionInContest getQuestionInContest(int contestId, int questionId, int ageGroupId, String lang);

    List<QuestionHeader> getQuestionsForContest(int contestId, int ageGroupId, String lang);

    void editTranslation(int questionId, String lang, String newTitle, String newSolution);

    void addTranslation(int questionId, String lang);

    void addTranslation(int questionId, Translation translation);

    void removeTranslation(int questionId, String lang);

    List<QuestionI18n> getQuestionI18n(int questionId);

    void editQuestion(int questionId, AnswerType type, String typeExtra, String externalId);

    void setQuestionAgeGroups(int contestId, int questionId, List<Integer> ageGroups);

    boolean questionExists(String externalId); // TODO not needed?

    boolean questionAlreadyUploaded (int questionId, String lang);

    enum FileType {
        QUESTION, FEEDBACK
    }

    void setUploaded (int questionId, String lang, FileType fileType);

    String getMagic (int questionId, FileType fileType);

}
