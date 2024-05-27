/*
 * ContestOrderController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import controllers.OrganiserOnlyController;
import deputies.contest.ContestOrderDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class ContestOrderController extends OrganiserOnlyController<ContestOrderDeputy> {
    public ContestOrderController() {
        super(ContestOrderDeputy::new);
    }

    public Result getQuestionSet(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).getQuestionSet(contestId, ageGroupId);
    }

    public Result updateOrder(Http.Request request, int contestId, int ageGroupId, int n1, int n2) {
        return createDeputy(request).updateOrder(contestId, ageGroupId, n1, n2);
    }

    public Result updateOrderByDifficulty(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).updateOrderByDifficulty(contestId, ageGroupId);
    }

}
