/*
 * HomeController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.home;

import common.DataAccessController;
import deputies.home.HomeDeputy;
import play.mvc.Http;
import play.mvc.Result;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends DataAccessController<HomeDeputy> {

    public HomeController() {
        super(HomeDeputy::new);
    }

    public Result index(Http.Request request) {
        return createDeputy(request).index();
    }

}
