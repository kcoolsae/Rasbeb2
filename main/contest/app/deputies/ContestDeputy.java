/*
 * ContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies;

import be.ugent.rasbeb2.db.dto.QuestionInSet;
import common.Session;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

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

    /**
     * Return position of the question with the given id in the set of questions
     */
    protected int indexOfId(List<QuestionInSet> questions, int questionId) {
        int pos = 0;
        int questionSetSize = questions.size();
        if (questionId != 0) {
            while (pos < questionSetSize && questions.get(pos).id() != questionId) {
                pos++;
            }
            if (pos == questionSetSize) {
                // if not found (should not happen) revert to question 0
                pos = 0;
            }
        }
        return pos;
    }

}
