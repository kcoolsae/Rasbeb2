/*
 * UserDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.dto.User;

public interface UserDao {

    int getCurrentUserId();

    /**
     * Create a new user. Returns the user id of the newly created user. Password must
     * be set separately.
     */
    int createUser (String name, String email, Role role);

    /**
     * Create a new teacher. User must be made separately.
     */
    void createTeacher(int userId, int schoolId);

    /**
     * Update the password for the given user.
     */
    void updatePassword(int userId, String password);

    void updateUsername(int userId, String username);

    /**
     * Returns the user for the given credentials.
     * @return user, or null if credentials were not valid
     */
    User getUser(String login, String password);

    /**
     * Checks whether the current user has the given password
     */
    boolean hasPassword(String password);

    /**
     * Returns the user for the given email.
     */
    User getUser(String email);

    User getUser(int userId);

    //User getUser();

    /**
     * Returns whether the given email address is known in the database.
     */
    boolean isKnownEmailAddress(String email);
}
