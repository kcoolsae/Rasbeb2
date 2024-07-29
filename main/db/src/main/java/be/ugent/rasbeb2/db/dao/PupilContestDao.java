/*
 * PupilDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.ContestForAnonTable;
import be.ugent.rasbeb2.db.dto.ContestForPupilTable;
import be.ugent.rasbeb2.db.dto.ContestWithAgeGroup;

import java.util.List;

/**
 * Data access object for contest related information intended for pupils
 */
public interface PupilContestDao {

    /**
     * Returns contests in which the current pupil can participate or has participated.
     */
    List<ContestForPupilTable> getContests();

    /**
     * Returns a list of all contests that can be taken by an anonymous user
     */
    List<ContestForAnonTable> getOpenPublicContests(String lang);

    /**
     * Returns information in the given language of the given contest and age group combination
     */
    ContestWithAgeGroup getContestWithAgeGroup(int contestId, int ageGroupId, String lang);

}
