/*
 * TeacherWithSchool.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record TeacherWithSchool(int id, String name, String email, Role role, boolean disabled, String schoolName, int schoolId, String schoolTown) {

    @Getter
    @AllArgsConstructor
    public enum Field implements FieldEnum<TeacherWithSchool> {
        NAME("user_name"),
        EMAIL("user_email"),
        DISABLED("user_disabled"),
        SCHOOL_NAME("school_name"),
        TOWN("school_town");

        private final String fieldName;
    }
}
