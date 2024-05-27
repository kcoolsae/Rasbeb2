/*
 * ContestSettingsController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import controllers.OrganiserOnlyController;
import deputies.contest.ContestSettingsDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class ContestSettingsController extends OrganiserOnlyController<ContestSettingsDeputy> {

    public ContestSettingsController() {
        super(ContestSettingsDeputy::new);
    }

    public Result newContestForm (Http.Request request) {
        return createDeputy(request).newContestForm();
    }

    public Result newContest (Http.Request request) {
        return createDeputy(request).newContest();
    }

    public Result settingsForm (Http.Request request, int contestId) {
        return createDeputy(request).settingsForm(contestId);
    }

    public Result titleSettings(Http.Request request, int contestId) {
        return createDeputy(request).titleSettings(contestId);
    }

    public Result ageGroupSettings(Http.Request request, int contestId) {
        return createDeputy(request).ageGroupSettings(contestId);
    }

}
