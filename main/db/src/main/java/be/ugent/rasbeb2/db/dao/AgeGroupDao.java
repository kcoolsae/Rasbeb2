/*
 * AgeGroupDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.AgeGroup;
import be.ugent.rasbeb2.db.dto.AgeGroupWithDuration;

import java.util.List;

/**
 * Handles age groups and age groups as relating to contests
 */
public interface AgeGroupDao {

    List<AgeGroup> getAllAgeGroups(String lang);

    /**
     * Return the list of age groups for which the contest is organised.
     * @param lang Language to be used for the age group titles
     */
    List<AgeGroup> getAgeGroups(int contestId, String lang);

    /**
     * Return the list of all age groups with duration for which the contest is organised. Duration
     * can be null if the contest is not organised for this age group.
     * @param lang Language to be used for the age group titles
     */
    List<AgeGroupWithDuration> getAgeGroupsWithDuration(int contestId, String lang);

    void removeAgeGroup(int contestId, int ageGroupId);

    void updateDuration(int contestId, int ageGroupId, int duration);

}
