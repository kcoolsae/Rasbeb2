/*
 * OrganiserOnlyDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies;

import be.ugent.caagt.play.controllers.Controller;
import common.DataAccessDeputy;
import controllers.OrganiserOnlyController;

/**
 * Base class for deputies of {@link controllers.OrganiserOnlyController}.
 */
public class OrganiserOnlyDeputy extends DataAccessDeputy {

    @Override
    public void setParent (Controller<?> parent) throws RuntimeException {
        if (! (parent instanceof OrganiserOnlyController)) {
            throw new RuntimeException("Parent of " + getClass() + " should be instance of OrganiserOnlyController");
        }
    }
}
