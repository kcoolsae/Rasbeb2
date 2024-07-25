/*
 * PupilDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dto.Event;
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
        return ok(show_pupil.render(dac().getEventDao().getEventHeader(eventId), this));
    }

    /**
     * Start participation of this pupil in the given contest.
     */
    public Result start(int eventId) {
        int pupilId = getCurrentUserId();
        int contestId = dac().getParticipationDao().create(eventId, pupilId);
        LOGGER.info("{} {} start event", pupilId, eventId);
        return firstParticipation(contestId);
    }

    /**
     * Initiates showing of feedback from the pupils home page
     */
    public Result showFeedback(int contestId) {
        LOGGER.info("{} {} init feedback", getCurrentUserId(), contestId);
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
                .addingToSession(request, Session.CONTEST, String.valueOf(contestId));
    }

    /**
     * Show the page that allows the pupil to take over the participation of another pupil.
     */
    public Result takeOver(int eventId) {
        Event event = dac().getEventDao().getEvent(eventId);
        LOGGER.info("{} {} take over", getCurrentUserId(), eventId);
        return ok(show_take_over.render(event.header(), event.contestId(), this));
    }

}
