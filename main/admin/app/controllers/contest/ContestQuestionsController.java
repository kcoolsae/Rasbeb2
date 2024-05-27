/*
 * ContestQuestionsController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import be.ugent.caagt.play.binders.PSF;
import controllers.OrganiserOnlyController;
import play.mvc.Http;
import play.mvc.Result;
import deputies.contest.ContestQuestionsDeputy;
public class ContestQuestionsController extends OrganiserOnlyController<ContestQuestionsDeputy> {
    public ContestQuestionsController() {
        super(ContestQuestionsDeputy::new);
    }

    public Result questionSelection(Http.Request request, int contestId) {
        return createDeputy(request).questionSelection(contestId);
    }

    public Result listQuestions(Http.Request request, PSF psf, int contestId) {
        return createDeputy(request).listQuestions(psf, contestId);
    }

    public Result resizeQuestions(Http.Request request, PSF psf, int contestId) {
        return createDeputy(request).resizeQuestions(psf, contestId);
    }

    public Result actionQuestions(Http.Request request, PSF psf, int contestId) {
        return createDeputy(request).actionQuestions(psf, contestId);
    }

}
