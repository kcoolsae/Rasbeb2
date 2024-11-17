/*
 * ContestToolsController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.contest;

import controllers.OrganiserOnlyController;
import deputies.contest.ContestToolsDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class ContestToolsController extends OrganiserOnlyController<ContestToolsDeputy> {

    public ContestToolsController() {
        super(ContestToolsDeputy::new);
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

    public Result printWinners(Http.Request request, int contestId) {
        return createDeputy(request).printWinners(contestId);
    }

    public Result showAnomalyTools(Http.Request request, int contestId) {
        return createDeputy(request).showAnomalyTools(contestId);
    }

    public Result listAnomaliesHour(Http.Request request, int contestId) {
        return createDeputy(request).listAnomaliesHour(contestId);
    }

    public Result listAnomaliesWeekend(Http.Request request, int contestId) {
        return createDeputy(request).listAnomaliesWeekend(contestId);
    }

    public Result listAnomaliesDay(Http.Request request, int contestId) {
        return createDeputy(request).listAnomaliesDay(contestId);
    }

    public Result downloadParticipationSheet(Http.Request request, int contestId) {
        return createDeputy(request).downloadParticipationSheet(contestId);
    }

    public Result chartMarks(Http.Request request, int contestId, int ageGroupId) {
        return createDeputy(request).chartMarks(contestId, ageGroupId);
    }
}
