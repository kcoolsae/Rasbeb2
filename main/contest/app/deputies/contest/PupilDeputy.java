/*
 * PupilDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import common.Session;
import controllers.contest.routes;
import deputies.ContestDeputy;
import play.mvc.Result;
import views.html.contest.show_pupil;
import views.html.contest.show_take_over;

public class PupilDeputy extends ContestDeputy {

    // TODO: limit access to events of pupil?

    /**
     * Page with the button to start the contest.
     */
    public Result show(int eventId) {
        return ok(show_pupil.render(dac().getPupilDao().getContest(getPupilId(), eventId), this));
    }

    /**
     * Start participation of this pupil in the given contest.
     */
    public Result start(int eventId, int contestId, int ageGroupId) {
        dac().getParticipationDao().create(eventId, contestId, ageGroupId, getLanguage(), getPupilId());
        return firstParticipation(contestId);
    }

    /**
     * Initiates showing of feedback from the pupils home page
     */
    public Result showFeedback(int contestId) {
        return redirect(routes.FeedbackController.show())
                .removingFromSession(request, Session.CONTEST) // just to make sure
                .addingToSession(request, Session.FEEDBACK, String.valueOf(contestId));
    }

    /**
     * Finalize first participation of this pupil in the given contest.
     */
    public Result firstParticipation(int contestId) {
        return redirect(controllers.contest.routes.ParticipationController.question(0))
                .removingFromSession(request, Session.FEEDBACK) // just to make sure
                .addingToSession(request, Session.CONTEST, Integer.toString(contestId));
    }

    /**
     * Show the page that allows the pupil to take over the participation of another pupil.
     */
    public Result takeOver(int eventId) {
        return ok(show_take_over.render(dac().getPupilDao().getContest(getPupilId(), eventId), this));
    }

}
