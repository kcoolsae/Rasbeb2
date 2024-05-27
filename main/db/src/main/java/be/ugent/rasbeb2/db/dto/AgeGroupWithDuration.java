/*
 * AgeGroupWithDuration.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

/**
 * Combines an age group with a duration. Duration can be null if the contest is not organised for this age group.
 */
public record AgeGroupWithDuration(AgeGroup ageGroup, Integer duration) {

}
