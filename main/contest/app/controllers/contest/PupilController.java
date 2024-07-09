/*
 * PupilController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import common.DataAccessController;
import controllers.ForcedRedirect;
import controllers.PupilOnly;
import deputies.contest.PupilDeputy;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

/**
 * Intermediate pages for pupils, before taking part in a contest or viewing feedback.
 */
@With({PupilOnly.class, ForcedRedirect.class})
public class PupilController extends DataAccessController<PupilDeputy> {

    public PupilController() {
        super(PupilDeputy::new);
    }

    public Result show(Http.Request request, int eventId) {
        return createDeputy(request).show(eventId);
    }

    public Result showFeedback(Http.Request request, int contestId) {
        return createDeputy(request).showFeedback(contestId);
    }

    public Result takeOver(Http.Request request, int eventId) {
        return createDeputy(request).takeOver(eventId);
    }

    public Result start(Http.Request request, int eventId) {
        return createDeputy(request).start(eventId);
    }

    public Result firstParticipation(Http.Request request, int contestId) {
        return createDeputy(request).firstParticipation(contestId);
    }
}
