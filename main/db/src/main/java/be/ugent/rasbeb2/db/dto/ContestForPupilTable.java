/*
 * ContestForPupilTable.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.sql.Timestamp;

public record ContestForPupilTable(Event event, int contestId, int ageGroupId, String ageGroupName,
                                   boolean participationClosed, Timestamp participationDeadline) {
    public boolean isFinished() {
        return participationClosed;
    }

    public boolean isBeingTaken() {
        return !participationClosed && participationDeadline != null;
    }
}
