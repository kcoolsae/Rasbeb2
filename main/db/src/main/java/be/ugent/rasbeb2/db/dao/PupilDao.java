/*
 * PupilDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.ContestForPupilTable;
import be.ugent.rasbeb2.db.dto.Pupil;

import java.util.List;

public interface PupilDao {

    /**
     * Create a new anonymous pupil. Returns the user id of the newly created pupil.
     */
    int createAnonymousPupil();

    /**
     * Returns the pupil for the given name and password.
     */
    Pupil getPupil(int pupilId, String password);

    /**
     * Returns contests in which the user can participate or has participated.
     */
    List<ContestForPupilTable> getContests(int pupilId);

    ContestForPupilTable getContest(int pupilId, int eventId);
}
