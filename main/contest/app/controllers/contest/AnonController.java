/*
 * AnonController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import common.DataAccessController;
import deputies.contest.AnonDeputy;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Intermediate pages for anonymous participation, before taking part in a contest.
 */
public class AnonController extends DataAccessController<AnonDeputy> {

    public AnonController() {
        super(AnonDeputy::new);
    }

    public Result show(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).show(contestId, ageGroupId);
    }

    public Result start(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).start(contestId, ageGroupId);
    }
}
