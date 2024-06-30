/*
 * ContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies;

import common.Session;
import play.mvc.Http;
import play.mvc.Result;

public class ContestDeputy extends common.DataAccessDeputy {

    protected Result redirectToIndex() {
        return redirect(controllers.auth.routes.HomeController.index());
    }

    protected Result redirectToContest() {
        return redirect(controllers.contest.routes.ParticipationController.question(0));
    }

    protected Result redirectToFeedback() {
        return redirect(controllers.contest.routes.FeedbackController.show());
    }

    protected boolean isUserLoggedIn() {
        return request.session().get(Session.ID).isPresent();
    }

    protected boolean pupilLoggedIn() {
        Http.Session session = request.session();
        return session.get(Session.ID).isPresent() && session.get(Session.NAME).isPresent();
    }

    protected int getPupilId() {
        return Integer.parseInt(findInSession(Session.ID));
    }

}
