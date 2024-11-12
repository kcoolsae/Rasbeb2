/*
 * EventController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.event;

import controllers.TeacherOnlyController;
import controllers.ActiveYearOnly;
import deputies.event.EventDeputy;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;


public class EventController extends TeacherOnlyController<EventDeputy> {
    public EventController() {
        super(EventDeputy::new);
    }

    @With(ActiveYearOnly.class)
    public Result addEvent(Http.Request request, int contestId, int ageGroupId, String lang) {
        return createDeputy(request).addEvent(contestId, ageGroupId, lang);
    }

    public Result removeEvent(Http.Request request, int eventId) {
        return createDeputy(request).removeEvent(eventId);
    }

    public Result getEvent(Http.Request request, int eventId) {
        return createDeputy(request).getEvent(eventId);
    }

    @With(ActiveYearOnly.class)
    public Result editEvent(Http.Request request, int eventId) {
        return createDeputy(request).editEvent(eventId);
    }

    public Result downloadSelectedPupils(Http.Request request, int eventId) {
        return createDeputy(request).downloadSelectedPupils(eventId);
    }

    public Result downloadScores(Http.Request request, int eventId) {
        return createDeputy(request).downloadScores(eventId);
    }

    @With(ActiveYearOnly.class)
    public Result addPermissions(Http.Request request, int eventId, int classId, boolean fromHome) {
        return createDeputy(request).addPermissions(eventId, classId, fromHome);
    }

    public Result viewPermissions(Http.Request request, int eventId) {
        return createDeputy(request).viewPermissions(eventId);
    }

    @With(ActiveYearOnly.class)
    public Result addExtraPupil(Http.Request request, int eventId, boolean fromHome) {
        return createDeputy(request).addExtraPupil(eventId, fromHome);
    }

    @With(ActiveYearOnly.class)
    public Result openEvent(Http.Request request, int eventId) {
        return createDeputy(request).openEvent(eventId);
    }

    @With(ActiveYearOnly.class)
    public Result closeEvent(Http.Request request, int eventId) {
        return createDeputy(request).closeEvent(eventId);
    }

    public Result listEventContests(Http.Request request, int ageGroupId, String lang) {
        return createDeputy(request).listEventContests(ageGroupId, lang);
    }

    @With(ActiveYearOnly.class)
    public Result editParticipationDeadline(Http.Request request, int eventId, int contestId, int pupilId) {
        return createDeputy(request).editParticipationDeadline(eventId, contestId, pupilId);
    }

    @With(ActiveYearOnly.class)
    public Result openParticipation(Http.Request request, int eventId, int contestId, int pupilId) {
        return createDeputy(request).openParticipation(eventId, contestId, pupilId);
    }
}
