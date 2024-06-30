/*
 * ContestWithAgeGroup.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record ContestWithAgeGroup(int contestId, String contestTitle, int contestDuration, int ageGroupId, String ageGroupName, String ageGroupDescription) implements Comparable<ContestWithAgeGroup> {

    @Override
    public int compareTo(ContestWithAgeGroup contest) {
        int contestCompare = contest.contestId() - this.contestId;
        return contestCompare == 0 ? this.ageGroupId - contest.ageGroupId() : contestCompare;
    }

}
