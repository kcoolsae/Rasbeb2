/*
 * QuestionInContest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

/**
 * Question information needed when displaying a question in a contest
 */
public record QuestionInContest(int id, String title, AnswerType answerType, String magicQ, String typeExtra, int marksIfCorrect, int marksIfIncorrect){
}
