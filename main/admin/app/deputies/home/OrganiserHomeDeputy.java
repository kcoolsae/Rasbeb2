/*
 * OrganiserHomeDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.home;

import deputies.OrganiserOnlyDeputy;
import play.mvc.Result;
import views.html.home.home_organiser;

public class OrganiserHomeDeputy extends OrganiserOnlyDeputy {

    public Result index() {
        return ok(home_organiser.render(this));
    }
}
