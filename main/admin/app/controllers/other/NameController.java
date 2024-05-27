/*
 * NameController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.other;

import common.DataAccessController;
import deputies.other.NameDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class NameController extends DataAccessController<NameDeputy> {

    public NameController() {
        super(NameDeputy::new);
    }

    public Result changeNameForm(Http.Request request) {
        return createDeputy(request).changeNameForm();
    }

    public Result changeName(Http.Request request) {
        return createDeputy(request).changeName();
    }

}
