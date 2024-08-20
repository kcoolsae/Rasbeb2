/*
 * ContestController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import be.ugent.caagt.play.binders.PSF;
import controllers.OrganiserOnlyController;
import deputies.contest.ContestDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class ContestController extends OrganiserOnlyController<ContestDeputy> {
    public ContestController() {
        super(ContestDeputy::new);
    }

    public Result getContest(Http.Request request, int contestId) {
        return createDeputy(request).getContest(contestId);
    }

    public Result updateContestStatus (Http.Request request, int contestId) {
        return createDeputy(request).updateContestStatus(contestId);
    }

    public Result tools(Http.Request request, int contestId) {
        return createDeputy(request).tools(contestId);
    }

    public Result copyContest(Http.Request request, int contestId) {
        return createDeputy(request).copyContest(contestId);
    }

    public Result listLinks(Http.Request request, int contestId, String lang) {
        return createDeputy(request).listLinks(contestId, lang);
    }

    public Result listContests (Http.Request request) {
        return createDeputy(request).listContests();
    }

    public Result list(Http.Request request, PSF psf) {
        return createDeputy(request).list(psf);
    }

    public Result resize(Http.Request request, PSF psf) {
        return createDeputy(request).resize(psf);
    }

    public Result action(Http.Request request, PSF psf) {
        return createDeputy(request).action(psf);
    }


}
