/*
 * ParticipationInfo.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.time.Instant;

/**
 * Gathers information about a participation. Depending on the context, fields may be left null or zero.
 */
public record ParticipationInfo (
        int pupilId, String pupilName, Gender pupilGender,
        int age_group_id, String lang,
        int schoolId, String schoolName, String schoolTown,
        String className, Instant instant, int marks
) {

    // for use in ContestDao.getWinners
    public ParticipationInfo (String pupilName, String schoolName, String town, int marks) {
        this(0, pupilName, null,
                0, null,
                0, schoolName, town,
                null, null, marks);
    }

    // for use in AnomalyFinder
    public ParticipationInfo (int pupilId, String pupilName, int schoolId, String schoolName, String schoolTown, String className, Instant instant) {
        this(pupilId, pupilName, null,
                0, null,
                schoolId, schoolName, schoolTown,
                className, instant, 0);
    }

    public String schoolNameWithTown() {
        return School.nameWithTown(schoolName, schoolTown);
    }

}
