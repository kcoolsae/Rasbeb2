/*
 * ContestWithAgeGroup.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record ContestWithAgeGroup(Contest contest, AgeGroupWithDuration ageGroup) implements Comparable<ContestWithAgeGroup> {

    public int contestId() {
        return contest.id();
    }

    @Override
    public int compareTo(ContestWithAgeGroup other) {
        int contestCompare = other.contest.id() - this.contest.id();
        if (contestCompare == 0) {
            return this.ageGroup.id() - other.ageGroup.id();
        } else {
            return contestCompare;
        }
    }

}
