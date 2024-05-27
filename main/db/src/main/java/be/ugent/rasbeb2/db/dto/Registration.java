/*
 * Registration.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public record Registration(String email, String school, Timestamp expireDate) {

    @Getter
    @AllArgsConstructor
    public enum Field implements FieldEnum<Registration> {
        EMAIL("user_email"),
        SCHOOL("school_name"),
        EXPIRE("registration_expires");

        private final String fieldName;
    }

    public String formattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(expireDate);
    }
}
