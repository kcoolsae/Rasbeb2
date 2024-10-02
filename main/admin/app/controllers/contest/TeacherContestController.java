/*
 * TeacherContestController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import be.ugent.caagt.play.binders.PSF;
import controllers.TeacherOnlyController;
import deputies.contest.TeacherContestDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class TeacherContestController extends TeacherOnlyController<TeacherContestDeputy> {

    public TeacherContestController() {
        super(TeacherContestDeputy::new);
    }

    public Result getContestQuestion(Http.Request request, String language, int contestId, int ageGroupId, int questionId) {
        return createDeputy(request).getContestQuestion(language, contestId, ageGroupId, questionId);
    }

    public Result listContests(Http.Request request) {
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

    public Result getContest(Http.Request request, int contestId) {
        return createDeputy(request).getContest(contestId);
    }

    public Result showQuestions(Http.Request request, String lang, int contestId) {
        return createDeputy(request).showQuestions(lang, contestId);
    }

    public Result showParticipations(Http.Request request, int contestId) {
        return createDeputy(request).showParticipations(contestId);
    }
}
