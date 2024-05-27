/*
 * LanguageController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.other;

import be.ugent.caagt.play.controllers.BaseController;
import deputies.other.LanguageDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class LanguageController extends BaseController<LanguageDeputy> {

    public LanguageController() {
        super(LanguageDeputy::new);
    }

    public Result chooseLanguage(Http.Request request) {
        return createDeputy(request).chooseLanguage();
    }

    public Result setLanguage(Http.Request request) {
        return createDeputy(request).setLanguage();
    }

}
