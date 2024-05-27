/*
 * HomeDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.home;

import common.Session;
import common.DataAccessDeputy;
import play.mvc.Call;
import play.mvc.Result;

public class HomeDeputy extends DataAccessDeputy {

    private Result redirectPreserveFlash (Call call) {
        return redirect(call).withFlash(request.flash());
    }

    public Result index() {
        if (!isLanguageSet()) {
            return redirect(controllers.other.routes.LanguageController.chooseLanguage());
        } else if (userLoggedIn()) {
            return switch (getFromSession(Session.ROLE)) {
                case "ORGANISER" -> redirectPreserveFlash(controllers.home.routes.OrganiserHomeController.index());
                case "TEACHER" -> redirectPreserveFlash(controllers.home.routes.TeacherHomeController.index());
                default -> badRequest();
            };
        } else if (appNeedsInitialisation()) {
            return redirect(controllers.auth.routes.RegistrationController.showFirstOrganiser());
        } else {
            return redirectPreserveFlash(controllers.auth.routes.AuthenticationController.loginForm());
        }
    }

    private boolean userLoggedIn() {
        return findInSession(Session.ID) != null;
    }

    private boolean appNeedsInitialisation() {
        return dac().getOrganiserDao().noOrganisersRegistered();
    }
}
