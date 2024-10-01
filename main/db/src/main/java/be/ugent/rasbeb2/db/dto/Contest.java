/*
 * Contest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record Contest(int id, ContestType contestType, ContestStatus status, String title) {

    public Contest {
        if (title == null || title.isBlank()) {
            title = "Contest #" + id;
        }
    }

    public boolean isViewable() {
        return status == ContestStatus.OPEN || status == ContestStatus.CLOSED;
    }

    @Getter
    @AllArgsConstructor
    public enum Field implements FieldEnum<Contest> {
        TYPE("contest_type"),
        STATUS("contest_status"),
        TITLE("contest_title");

        private final String fieldName;
    }
}
