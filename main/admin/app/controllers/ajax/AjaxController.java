/*
 * AjaxController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.ajax;

import be.ugent.rasbeb2.db.util.PasswordGenerator;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class AjaxController extends Controller {

    /**
     * Ajax call that returns a random password.
     */
    public Result generatePassword(Http.Request ignored) {
        return ok(PasswordGenerator.generate());
    }
}
