/*
 * TeacherContestController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import controllers.TeacherOnlyController;
import deputies.contest.TeacherContestDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class TeacherContestController extends TeacherOnlyController<TeacherContestDeputy> {

    public TeacherContestController() {
        super(TeacherContestDeputy::new);
    }

    public Result listContests(Http.Request request, String language, int ageGroupId) {
        return createDeputy(request).listContests(language, ageGroupId);
    }

    public Result getContestQuestion(Http.Request request, String language, int contestId, int ageGroupId, int questionId) {
        return createDeputy(request).getContestQuestion(language, contestId, ageGroupId, questionId);
    }
}
