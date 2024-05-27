/*
 * Pupil.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record Pupil (int id, String name, Gender gender, String password) {

    public Pupil {
        if (name == null || name.isBlank()) {
            name = "Pupil #" + id;
        }
    }
}
