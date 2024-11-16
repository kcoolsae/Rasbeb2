/*
 * ParticipationInfoDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.ParticipationInfo;
import java.util.List;

/**
 * Provides access to all types of participation information.
 */
public interface ParticipationInfoDao {

    /**
     * Find participations after a given hour
     */
    List<ParticipationInfo> listAfterHour(int contestId, int hour);

    /**
     * Find participations in the weekend
     */
    List<ParticipationInfo> listInWeekend(int contestId);

    /**
     * Find participations at a given day of the month
     */
    List<ParticipationInfo> listAtDayOfMonth(int contestId, int day);

    /**
     * All participations
     */
    List<ParticipationInfo> listAll(int contestId);
}
