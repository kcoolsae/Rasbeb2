/*
 * ContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dto.Contest;
import be.ugent.rasbeb2.db.dto.ContestStatus;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.mvc.Call;
import play.mvc.Result;
import util.Table;
import views.html.contest.list_contests;
import views.html.contest.organiser_contest;

public class ContestDeputy extends OrganiserOnlyDeputy {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor // needed by Spring
    public static class StatusData {
        public ContestStatus status;
    }

    public Result getContest(int contestId) {
        Contest contest = dac().getContestDao().getContest(contestId, getLanguage());
        return ok(organiser_contest.render(
                formFromData(new StatusData(contest.status())),
                contest,
                this
        ));
    }

    public Result updateContestStatus(int contestId) {
        dac().getContestDao().changeStatus(
                contestId, formFromRequest(StatusData.class).get().status
        );
        success("contest.settings.success-status");
        return redirect(routes.ContestController.getContest(contestId));
    }

    public Result listContests() {
        return list(getInitialPSF(Contest.Field.TITLE, false));
    }

    /* ======================
     * PAGED TABLE for CONTESTS
     * ====================== */

    public Result list(PSF psf) {
        return ok(list_contests.render(
                getPage(dac().getContestDao().findContests(getLanguage()), psf, Contest.Field.class),
                new Table(psf) {
                    public Call list(PSF newPsf) {
                        return routes.ContestController.list(newPsf);
                    }

                    public Call resize() {
                        return routes.ContestController.resize(psf());
                    }

                    public Call action() {
                        return routes.ContestController.action(psf());
                    }
                },
                this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.ContestController::list);
    }

    public Result action(PSF psf) {
        // filter button - not checked
        return redirect(routes.ContestController.list(
                psf.refilter(getStringMapFromForm(Contest.Field.class))
        ));
    }


}
