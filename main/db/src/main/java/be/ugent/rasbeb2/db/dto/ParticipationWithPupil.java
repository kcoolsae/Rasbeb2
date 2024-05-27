/*
 * ParticipationWithPupil.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.time.Instant;

public record ParticipationWithPupil(int pupilId, int contestId, String pupilName, boolean closed, Instant deadline,
                                     int marks, int maxMarks) {

    public ParticipationWithPupil {
        if (pupilName == null || pupilName.isBlank()) {
            pupilName = "Pupil #" + pupilId;
        }
    }

    public boolean isValid() {
        return maxMarks > 0;
    }
}
