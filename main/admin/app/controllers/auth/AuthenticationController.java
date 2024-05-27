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
import deputies.auth.AuthenticationDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class AuthenticationController extends DataAccessController<AuthenticationDeputy> {

    @Override
    protected AuthenticationDeputy createDeputy(Http.Request request) {
        return super.createDeputy(request);
    }

    public AuthenticationController() {
        super(AuthenticationDeputy::new);
    }

    public Result loginForm(Http.Request request) {
        return createDeputy(request).loginForm();
    }

    public Result login(Http.Request request) {
        return createDeputy(request).login();
    }

    public Result logout(Http.Request request) {
        return createDeputy(request).logout();
    }

    public Result changePasswordForm(Http.Request request) {
        return createDeputy(request).changePasswordForm();
    }

    public Result changePassword(Http.Request request) {
        return createDeputy(request).changePassword();
    }
}
