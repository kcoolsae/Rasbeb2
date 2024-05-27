/*
 * RegistrationDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.Registration;

public interface RegistrationDao {

    /**
     * Adds a password request for the given email address with a validity of 1 hour.
     * @return The token stored in the database to validate this request
     */
    String addPasswordRequest (String email);

    /**
     * Check whether e-mail/token combination exists in the list of
     * password requests and whether it is not yet expired
     */
    boolean isValidPasswordRequest (String email, String token);

    /**
     * Remove the password request for the given e-mail address
     */
    void deletePasswordRequest(String email);

    /**
     * Adds a teacher registration for the given email address with a validity of 24 hours.
     * @return The token stored in the database to validate this request
     */
    String addRegistration(String email, int schoolId);

    /**
     * Check whether e-mail/token/school combination exists in the list of
     * registrations and whether it is not yet expired
     */
    boolean isValidRegistration(String email, String token, int schoolId);

    /**
     * Remove the registration for the given e-mail address
     */
    void deleteRegistration(String email);

    interface RegistrationFinder extends Finder<Registration, Registration.Field, RegistrationDao.RegistrationFinder> {}
    RegistrationDao.RegistrationFinder findRegistrations ();


    void deleteExpiredRegistrations();
}
