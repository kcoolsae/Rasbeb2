/*
 * EventDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.event;

import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dao.EventDao;
import be.ugent.rasbeb2.db.dto.*;
import be.ugent.rasbeb2.db.poi.PupilSheetWriter;
import be.ugent.rasbeb2.db.poi.ScoreSheetWriter;
import common.LanguageInfo;
import controllers.event.routes;
import deputies.TeacherOnlyDeputy;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;
import views.html.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventDeputy extends TeacherOnlyDeputy {

    /**
     * Display event information
     */
    public Result getEvent(int eventId) {
        EventDao dao = dac().getEventDao();
        if (dao.isCorrectSchool(eventId)) {
            Event event = dao.getEvent(eventId);
            return switch (event.getExtendedStatus()) {
                case PENDING_FREE, PENDING_BLOCKED -> ok(teacher_pending_event.render(
                        event,
                        dac().getEventDao().listClassesWithPermissions(event.id()),
                        this)
                );
                case OPEN -> ok(teacher_open_event.render(
                        event,
                        dac().getEventDao().getParticipations(event.id()),
                        this)
                );
                case CLOSED_FREE, CLOSED_BLOCKED -> ok(teacher_closed_event.render(
                        event,
                        dac().getEventDao().getParticipations(event.id()),
                        this)
                );
            };
        } else { // when school of event and school of teacher don't match
            error("events.event.error-no-access");
            return redirect(controllers.home.routes.TeacherHomeController.index());
        }
    }

    public static final int DEFAULT_MINUTES_TO_ADD = 10;

    @Getter
    @Setter
    public static class AddEventData {
        public String title;
    }

    public Result addEvent(int contestId, int ageGroupId, String lang) {
        Form<AddEventData> form = formFromRequest(AddEventData.class);
        if (form.hasErrors()) {
            return badRequest(); // this should not happen
        } else {
            String title = form.get().title;
            dac().getEventDao().addEvent(contestId, ageGroupId, getCurrentYearId(), title, lang);
            success("event.add.message");
            return redirect(controllers.home.routes.TeacherHomeController.index());
        }
    }

    @Getter
    @Setter
    public static class EventPermissionData {
        public List<Integer> checked;

        public EventPermissionData() {
            checked = new ArrayList<>(); // so that the form also works when no checkboxes are checked
        }
    }

    public Result addPermissions(int eventId, int classId, boolean fromHome) {
        EventPermissionData data = formFromRequest(EventPermissionData.class).get();
        dac().getEventDao().updateClassPermissions(eventId, classId, data.checked);
        success("event.registrations.message");
        if (fromHome) {
            return redirect(routes.EventController.getEvent(eventId));
        } else {
            return redirect(routes.EventController.viewPermissions(eventId));
        }
    }

    public Result viewPermissions(int eventId) {
        EventDao dao = dac().getEventDao();
        return ok(permissions_event.render(
                dao.getEventHeader(eventId),
                dac().getEventDao().listClassesWithPermissions(eventId),
                this
        ));
    }

    public Result downloadSelectedPupils(int eventId) {
        String title = dac().getEventDao().getEventTitle(eventId);
        List<PupilInClass> pupils = dac().getClassesDao().getPupilsInClass(dac().getEventDao().getSelectedPupils(eventId));
        return ok(new PupilSheetWriter(this::i18n).write(pupils, title))
                .as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .withHeader(
                        "Content-Disposition",
                        "attachment; filename=" + i18n("spreadsheet.filename.registrations") + "-" + eventId + ".xlsx"
                );
    }

    public Result downloadScores(int eventId) {
        String title = dac().getEventDao().getEventTitle(eventId);
        List<PupilWithScore> pupils = dac().getEventDao().getPupilsWithScore(eventId);
        return ok(new ScoreSheetWriter(this::i18n).write(pupils, title))
                .as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .withHeader(
                        "Content-Disposition",
                        "attachment; filename=" + i18n("spreadsheet.filename.scores") + "-" + eventId + ".xlsx"
                );
    }

    @Getter
    @Setter
    public static class ExtraPupilData {
        public int classId;
        public String name;
        public Gender gender;
        public String password;
    }

    public Result addExtraPupil(int eventId, boolean fromHome) {
        Form<ExtraPupilData> form = formFromRequest(ExtraPupilData.class);
        if (form.hasErrors()) {
            return badRequest(); // only happens when form is tampered with
        }
        ExtraPupilData data = form.get();
        int pupilId = dac().getClassesDao().addPupil(data.classId, data.name, data.gender, data.password);
        dac().getEventDao().selectPupil(eventId, pupilId);
        success("event.registrations.success");
        if (fromHome) {
            return redirect(routes.EventController.getEvent(eventId));
        } else {
            return redirect(routes.EventController.viewPermissions(eventId));
        }
    }

    @Getter
    @Setter
    public static class EditEventData {
        public String title;
    }

    public Result editEvent(int eventId) {
        EventDao dao = dac().getEventDao();
        if (dao.isCorrectSchool(eventId)) {
            Form<EditEventData> form = formFromRequest(EditEventData.class);
            if (form.hasErrors()) {
                return badRequest(); // should not happen
            } else {
                dao.editEvent(eventId, form.get().title);
                return redirect(routes.EventController.getEvent(eventId));
            }
        } else { // when school of event and school of teacher don't match
            error("event.event.error-no-access");
            return redirect(controllers.home.routes.TeacherHomeController.index());
        }
    }

    public Result openEvent(int eventId) {
        // post but no form
        EventDao dao = dac().getEventDao();
        if (dao.isCorrectSchool(eventId)) {
            dao.openEvent(eventId);
            return redirect(routes.EventController.getEvent(eventId));
        } else {
            return badRequest();
        }
    }

    public Result closeEvent(int eventId) {
        // post but no form
        EventDao dao = dac().getEventDao();
        if (dao.isCorrectSchool(eventId)) {
            dao.closeEvent(eventId);
            return redirect(routes.EventController.getEvent(eventId));
        } else {
            return badRequest();
        }
    }

    public Result listEventContests(int ageGroupId, String lang) {
        List<AgeGroup> ageGroups = dac().getAgeGroupDao().getAllAgeGroups(lang);
        if (ageGroupId == 0) {
            // default value when no id given
            ageGroupId = ageGroups.getFirst().id();
        }
        return ok(new_event.render(
                LanguageInfo.get(lang),
                getUILanguagesInfo(),
                ageGroupId,
                ageGroups,
                dac().getContestDao().getOrganisableContests(ageGroupId, lang),
                this)
        );
    }

    @Getter
    @Setter
    public static class EditParticipationData {
        public int minutesToAdd;
    }

    public Result editParticipationDeadline(int eventId, int contestId, int pupilId) {
        Form<EditParticipationData> form = formFromRequest(EditParticipationData.class);
        if (form.hasErrors()) { // should not happen!
            return badRequest();
        } else {
            EditParticipationData data = form.get();
            dac().getEventDao().participationAddExtraMinutes(contestId, pupilId, data.minutesToAdd);
            success("event.extra-minutes.success");
            return redirect(routes.EventController.getEvent(eventId));
        }
    }

    public Result openParticipation(int eventId, int contestId, int pupilId) {
        // participations can only be reopened if event is open
        EventDao dao = dac().getEventDao();
        if (dao.isOpen(eventId)) {
            // post but no form
            dao.reopenParticipation(contestId, pupilId);
            success("event.participations.success-reopen");
            return redirect(routes.EventController.getEvent(eventId));
        } else {
            return badRequest();
        }
    }
}
