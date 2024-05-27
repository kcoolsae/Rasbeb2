/*
 * OrganiserHomeController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.home;

import controllers.OrganiserOnlyController;
import deputies.home.OrganiserHomeDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class OrganiserHomeController extends OrganiserOnlyController<OrganiserHomeDeputy> {

    public OrganiserHomeController() {
        super(OrganiserHomeDeputy::new);
    }

    public Result index(Http.Request request) {
        return createDeputy(request).index();
    }
}
