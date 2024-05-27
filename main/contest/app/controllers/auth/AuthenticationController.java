/*
 * AuthenticationController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.auth;

import common.DataAccessController;
import controllers.ForcedRedirect;
import deputies.auth.AuthenticationDeputy;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

public class AuthenticationController extends DataAccessController<AuthenticationDeputy> {

    @Override
    protected AuthenticationDeputy createDeputy(Http.Request request) {
        return super.createDeputy(request);
    }

    public AuthenticationController() {
        super(AuthenticationDeputy::new);
    }

    @With(ForcedRedirect.class)
    public Result loginForm(Http.Request request) {
        return createDeputy(request).loginForm();
    }

    @With(ForcedRedirect.class)
    public Result login(Http.Request request) {
        return createDeputy(request).login();
    }

    public Result logout(Http.Request request) {
        return createDeputy(request).logout();
    }
}
