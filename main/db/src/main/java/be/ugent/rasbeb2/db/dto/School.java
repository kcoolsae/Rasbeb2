/*
 * School.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record School (int id, String name, String street, String zip, String town) {

    public School {
        if (name == null || name.isBlank()) {
            name = "School #" + id;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Field implements FieldEnum<School> {
        NAME("school_name"),
        STREET("school_street"),
        TOWN("school_town"),
        ZIP("school_zip");

        private final String fieldName;
    }

    public String nameWithTown() {
        if (name.toLowerCase().contains(town.toLowerCase())) {
            return name;
        } else {
            return name + " (" + town + ")";
        }
    }
}
