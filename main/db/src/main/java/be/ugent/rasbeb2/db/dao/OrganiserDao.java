/*
 * OrganiserDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

public interface OrganiserDao {

    /**
     * Returns true if there are no registered organisers in the system.
     * Used to initialise the database when the application is first launched
     */
    boolean noOrganisersRegistered ();

}
