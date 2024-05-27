/*
 * QuestionWithAgeGroups.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents a question together with all age groups for which this question is used.
 */
public record QuestionWithAgeGroups(int id, String title, String externalId, List<Integer> ageGroups) {

    @Getter
    @AllArgsConstructor
    public enum Field implements FieldEnum<QuestionWithAgeGroups> {
        TITLE("question_title"),
        EXTERNAL_ID("question_external_id");

        private final String fieldName;
    }
}
