/*
 * Participation.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.time.Instant;

public record Participation(int contestId, int pupilId, int ageGroupId, String lang, Integer eventId, boolean closed, int timeLeftInSeconds, int durationInSeconds, Instant deadline) {
}
