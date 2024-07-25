/*
 * AnonDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dto.ContestWithAgeGroup;
import common.Session;
import deputies.ContestDeputy;
import play.mvc.Result;
import views.html.contest.show_anon;

public class AnonDeputy extends ContestDeputy {

    /**
     * Page with button to start the contest.
     */
    public Result show(int contestId, int ageGroupId) {
        if (isUserLoggedIn()) {
            // anonymous users only login later
            return badRequest();
        } else {
            ContestWithAgeGroup contest = dac().getPupilContestDao().getContestWithAgeGroup(contestId, ageGroupId, getLanguage());
            return ok(show_anon.render(contest, this));
        }
    }

    /**
     * Start the anonymous participation.
     */
    public Result start(int contestId, int ageGroupId) {
       if (isUserLoggedIn()) {
            // anonymous users only login later
            return badRequest();
        } else {
           int pupilId = dac().getUserDao().createAnonymousPupil();
           dac().getParticipationDao().create(contestId, ageGroupId, getLanguage(), pupilId);
           return redirect(controllers.contest.routes.ParticipationController.question(0))
                   .addingToSession(request, Session.CONTEST, Integer.toString(contestId))
                   .addingToSession(request, Session.ID, Integer.toString(pupilId));
       }
    }

}
