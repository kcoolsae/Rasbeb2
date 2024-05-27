/*
 * QuestionHeader.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record QuestionHeader(int id, AnswerType answerType, String title, String externalId) {

    public QuestionHeader {
        if (title == null || title.isBlank()) {
            title = "Question #" + id;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Field implements FieldEnum<QuestionHeader> {
        ID("question_external_id"),
        TITLE("question_title"),
        TYPE("question_type"); // do we need this?

        private final String fieldName;
    }
}
