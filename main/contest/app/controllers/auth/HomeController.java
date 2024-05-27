/*
 * HomeController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.auth;

import common.DataAccessController;
import deputies.auth.HomeDeputy;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Starting pages for pupils and for anonymous participation.
 */
public class HomeController extends DataAccessController<HomeDeputy> {

    public HomeController() {
        super(HomeDeputy::new);
    }

    public Result index(Http.Request request) {
        return createDeputy(request).index();
    }

    public Result anonymous(Http.Request request) {
        return createDeputy(request).anonymous();
    }

}
