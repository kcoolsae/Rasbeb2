/*
 * FeedbackController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import deputies.contest.FeedbackDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class FeedbackController extends common.DataAccessController<FeedbackDeputy> {

    public FeedbackController() {
        super(FeedbackDeputy::new);
    }

    public Result show(Http.Request request) {
        return createDeputy(request).show();
    }

    public Result question(Http.Request request, int questionId) {
        return createDeputy(request).question(questionId);
    }

    public Result close(Http.Request request) {
        return createDeputy(request).close();
    }
}
