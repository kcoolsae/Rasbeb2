/*
 * Session.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common;

/**
 * Defines and documents all session variables
 */
public final class Session {

    /**
     * ID of the teacher/organiser currently logged in, or Bebras ID of
     * the pupil currently logged in (depending on the web application).
     * Not present when nobody is logged in. Non-zero integer. Note that anonymous users
     * are also given an ID.
     */
    public static final String ID = "id";

    /**
     * Role of the teacher or organiser currently logged in. Currently not used for pupils
     * @see be.ugent.rasbeb2.db.dto.Role
     */
    public static final String ROLE = "role";

    /**
     * Full name of the person currently logged in. An anonymous user has an ID but no name.
     */
    public static final String NAME = "name";

    /**
     * ID of the organiser that is mimicking a login
     */
    public static final String PARENT = "parent";

    /**
     * If present, indicates that the current pupil is participating in a contest. Contains
     * the ID of that contest. The session cannot contain both CONTEST and FEEDBACK at the same time.
     */
    public static final String CONTEST = "contest";

    /**
     * If present, indicates that the current pupil is consulting the feedback of a contest.
     * Contains the ID of that contest. The session cannot contain both CONTEST and FEEDBACK at the same time.
     */
    public static final String FEEDBACK = "feedback";

    /**
     * Id of the current year
     */
    public static final String YEAR = "year";

    /**
     * Text representation of the current year.
     */
    public static final String YEAR_NAME = "year-name";

    /**
     * Indicates whether the current year is active or not. (true or false)
     */
    public static final String YEAR_ACTIVE = "year-active";

    /**
     * School ID of the current user, only present when the user is a teacher.
     */
    public static final String SCHOOL_ID = "school";


}
