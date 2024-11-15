/*
 * AnomalyFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import java.time.Instant;
import java.util.List;

/**
 * Helps finding anomalies for public contest
 */
public interface AnomalyFinder {

    record Data (int pupilId, String pupilName, int schoolId, String schoolName, String schoolTown, String className, Instant instant) {

    }

    /**
     * Find participations after a given hour
     */
    List<Data> listAfterHour(int hour);

    /**
     * Find participations in the weekend
     */
    List<Data> listInWeekend();

    /**
     * Find participations at a given day of the month
     */
    List<Data> listAtDayOfMonth(int day);
}
