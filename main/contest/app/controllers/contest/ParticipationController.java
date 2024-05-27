/*
 * ParticipationController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import deputies.contest.ParticipationDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class ParticipationController extends common.DataAccessController<ParticipationDeputy> {

    public ParticipationController() {
        super(ParticipationDeputy::new);
    }

    public Result question(Http.Request request, int questionId) {
        return createDeputy(request).question(questionId);
    }

    public Result answer(Http.Request request, int questionId, int nextId) {
        return createDeputy(request).answer(questionId, nextId);
    }

    public Result close(Http.Request request) {
        return createDeputy(request).close();
    }
}
