/*
 * PupilWithScore.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record PupilWithScore(int pupilId, String name, String className, int marks, int maxMarks) {

    public PupilWithScore {
        if (name == null || name.isBlank()) {
            name = "Pupil #" + pupilId;
        }
    }
}
