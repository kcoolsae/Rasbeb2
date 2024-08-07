/*
 * HomeDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.auth;

import common.Session;
import deputies.ContestDeputy;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import views.html.home.home_anon;
import views.html.home.home_pupil;

public class HomeDeputy extends ContestDeputy {

    private Result redirectPreserveFlash(Call call) {
        return redirect(call).withFlash(request.flash());
    }

    /**
     * Starting page of the application. Dispatches according the session state.
     */
    public Result index() {
        // ensure language is set // TODO use action composition
        if (!isLanguageSet()) {
            return redirect(controllers.other.routes.LanguageController.chooseLanguage());
        } else if (isUserLoggedIn()) {
            return dispatchAccordingToSession(false);
        } else {
            return redirectPreserveFlash(controllers.auth.routes.AuthenticationController.loginForm());
        }
    }

    /**
     * Starting page for anonymous participation.
     */
    public Result anonymous() {
        // ensure language is set
        if (!isLanguageSet()) {
            return redirect(controllers.other.routes.LanguageController.chooseLanguage());
        } else if (isUserLoggedIn()) {
            return dispatchAccordingToSession(true);
        } else {
            // note that anonymous users are only logged in when in participation or feedback
            return ok(home_anon.render(
                    dac().getPupilContestDao().getOpenPublicContests(getLanguage()),
                    dac().getAgeGroupDao().getAllAgeGroups(getLanguage()),
                    this)
            );
        }
    }

    private Result dispatchAccordingToSession(boolean inAnonPage) {
        Http.Session session = request.session();
        if (session.get(Session.FEEDBACK).isPresent()) {
            return redirectToFeedback();
        } else if (session.get(Session.CONTEST).isPresent()) {
            return redirectToContest();
        } else if (session.get(Session.NAME).isPresent()) {
            if (inAnonPage) {
                warning("pupil.home-anon.error-logged-in");
                return redirect(controllers.auth.routes.HomeController.index());
            } else {
                return ok(home_pupil.render(
                        dac().getPupilContestDao().getContests(),
                        this)
                );
            }
        } else {
            // anonymous - should not happen because in this case either CONTEST or FEEDBACK would be present
            return badRequest();
        }
    }

}