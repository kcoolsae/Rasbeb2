/*
 * PasswordController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.auth;

import deputies.auth.PasswordDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class PasswordController extends EmailSendingController<PasswordDeputy> {

    public PasswordController() {
        super(PasswordDeputy::new);
    }

    public Result forgotPasswordForm(Http.Request request) {
        return createDeputy(request).forgotPasswordForm();
    }

    public Result forgotPassword(Http.Request request) {
        return createDeputy(request).forgotPassword();
    }

    public Result resetPasswordForm(Http.Request request, String token) {
        return createDeputy(request).resetPasswordForm(token);
    }

    public Result resetPassword(Http.Request request, String token) {
        return createDeputy(request).resetPassword(token);
    }

}
