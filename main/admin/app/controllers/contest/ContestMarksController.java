/*
 * ContestMarksController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import controllers.OrganiserOnlyController;
import deputies.contest.ContestMarksDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class ContestMarksController extends OrganiserOnlyController<ContestMarksDeputy> {
    public ContestMarksController() {
        super(ContestMarksDeputy::new);
    }

    public Result getQuestionSet(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).getQuestionSet(contestId, ageGroupId);
    }

    public Result updateMarks(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).updateMarks(contestId, ageGroupId);
    }
}
