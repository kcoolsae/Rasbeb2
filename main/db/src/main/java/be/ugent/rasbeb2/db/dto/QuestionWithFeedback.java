/*
 * QuestionWithFeedback.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record QuestionWithFeedback(int id, AnswerType answerType, String title, String magicQ, String magicF, String answer, String pageModel,
                                   int marks, int marksIfCorrect) {
}
