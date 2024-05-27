/*
 * TeacherOnlyDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies;

import be.ugent.caagt.play.controllers.Controller;
import be.ugent.rasbeb2.db.dto.Year;
import common.DataAccessDeputy;
import common.Session;
import controllers.TeacherOnlyController;

/**
 * Base class for deputies of {@link controllers.TeacherOnlyController}.
 */
public class TeacherOnlyDeputy extends DataAccessDeputy {

    @Override
    public void setParent(Controller<?> parent) throws RuntimeException {
        if (!(parent instanceof TeacherOnlyController)) {
            throw new RuntimeException("Parent of " + getClass() + " should be instance of TeacherOnlyController");
        }
    }

    /**
     * Get year from session
     */
    protected Year getCurrentYear() {
        return new Year(getCurrentYearId(), getFromSession(Session.YEAR_NAME));
    }

    /**
     * Check if year from session is an active year
     */
    public boolean isActiveYear() {
        return Boolean.parseBoolean(getFromSession(Session.YEAR_ACTIVE));
    }

    /**
     * Get year id from session
     */
    protected int getCurrentYearId() {
        return Integer.parseInt(getFromSession(Session.YEAR));
    }
}