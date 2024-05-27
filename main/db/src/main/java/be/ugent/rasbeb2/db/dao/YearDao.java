/*
 * YearDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.Year;

import java.util.List;

public interface YearDao {

    void createYear (String yearName);

    void removeYear(int yearId);

    void updateYear(int yearId, String yearName);

    /**
     * List all years, most recent first.
     */
    List<Year> listAllYears ();

    /**
     * Get the year with the given id, or the most recent year when the id is 0
     */
    Year getYear (int yearId);

    /**
     * Return the most recent year.
     */
    Year getRecentYear();

}
