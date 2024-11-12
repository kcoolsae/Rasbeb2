/*
 * TeacherContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.*;
import be.ugent.rasbeb2.db.poi.ScoreSheetWriter;
import common.LanguageInfo;
import controllers.contest.routes;
import deputies.TeacherOnlyDeputy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.Call;
import play.mvc.Result;
import play.twirl.api.Html;
import util.LanguagesWithSelection;
import util.Table;
import views.html.teachercontest.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides views of questions and feedback for teachers.
 */
public class TeacherContestDeputy extends TeacherOnlyDeputy {

    public Result getContestQuestion(String lang, int contestId, int ageGroupId, int questionId) {
        ContestWithAgeGroup cwa = dac().getPupilContestDao().getContestWithAgeGroup(contestId, ageGroupId, lang);
        Contest contest = cwa.contest();
        if (contest.isViewable()) {
            QuestionDao questionDao = dac().getQuestionDao();
            List<QuestionHeader> headers = questionDao.getQuestionsForContest(contestId, ageGroupId, lang);
            if (questionId == 0) {
                questionId = headers.getFirst().id();
            }
            Question question = questionDao.getQuestion(questionId, lang);
            boolean showFeedback = contest.contestType() != ContestType.OFFICIAL || contest.status() == ContestStatus.CLOSED;
            return ok(views.html.teachercontest.view_question.render(cwa, lang, question, headers, showFeedback, this));
        } else {
            return badRequest();
        }
    }

    public Result getContest(int contestId) {
        Contest contest = dac().getContestDao().getContest(contestId, getLanguage());
        return ok(contest_overview.render(
                contest,
                this
        ));
    }

    public Result showQuestions(String lang, int contestId) {
        ContestDao dao = dac().getContestDao();
        Contest contest = dao.getContest(contestId, lang);
            return ok(show_questions.render(
                    new LanguagesWithSelection(LanguageInfo.list(dao.getContestLanguages(contestId)), lang),
                    contest,
                    dac().getAgeGroupDao().getAgeGroups(contestId, getLanguage()),
                    this
            ));
    }

    public Result showParticipations(int contestId) {
        Contest contest = dac().getContestDao().getContest(contestId, getLanguage());
        Map<String, List<PupilWithScore>> map = new TreeMap<>();
        if (contest.status() == ContestStatus.CLOSED || contest.contestType() != ContestType.OFFICIAL) {
            List<PupilWithScore> participatingPupils = dac().getEventDao().getParticipatingPupils(contestId, getCurrentYearId());
            for (PupilWithScore pupil : participatingPupils) {
                map.computeIfAbsent(pupil.className(), k -> new ArrayList<>()).add(pupil);
            }
        }
        return ok(show_participations.render(
                contest,
                map,
                this
        ));
    }

    public Result showEvents(int contestId) {
        return ok (show_events.render(
                dac().getContestDao().getContest(contestId, getLanguage()),
                dac().getEventDao().listEventsForContest(contestId,getCurrentYearId()),
                this
        ));
    }

    public Result toggleHidden(int contestId, int pupilId) {
        dac().getEventDao().toggleParticipationVisibility(contestId, pupilId);
        return redirect(routes.TeacherContestController.showParticipations(contestId));
    }

    public Result downloadScores(int contestId) {
        Contest contest = dac().getContestDao().getContest(contestId, getLanguage());
        if (contest.status() == ContestStatus.CLOSED || contest.contestType() != ContestType.OFFICIAL) {
            String title = contest.title(); // TODO getContestTitle?
            List<PupilWithScore> pupils = dac().getEventDao().getParticipatingPupils(contestId, getCurrentYearId());
            return ok(new ScoreSheetWriter(this::i18n).write(pupils, title))
                    .as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .withHeader(
                            "Content-Disposition",
                            "attachment; filename=" + i18n("spreadsheet.filename.scores") + "-c-" + contestId + ".xlsx"
                    );
        } else {
            return badRequest();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class EventData {
        @Formats.NonEmpty
        @Constraints.Required
        public String title;
        @Constraints.Min(value=1, message = "event.add.error-age-group-required")
        public int ageGroupId;
        public String lang;
        public EventData() {
            // needed by Spring beans
        }
    }

    private Html newEventRender(int contestId, Form<EventData> form) {
        ContestDao dao = dac().getContestDao();
        String lang = getLanguage();
        return new_event.render(
                dao.getContest(contestId, lang),
                form,
                dac().getAgeGroupDao().getAgeGroups(contestId, lang),
                LanguageInfo.list(dao.getContestLanguages(contestId)),
                this
        );
    }

    public Result newEventForm(int contestId) {
        String lang = getLanguage();
        return ok(newEventRender(contestId, formFromData(new EventData("", 0, lang))));
    }

    public Result newEvent(int contestId) {
        Form<EventData> form = formFromRequest(EventData.class);
        if (form.hasErrors()) {
            return badRequest(newEventRender(contestId, form));
        } else {
            EventData data = form.get();
            dac().getEventDao().addEvent(contestId, data.ageGroupId, getCurrentYearId(), data.title, data.lang);
            success("event.add.message");
            return redirect(routes.TeacherContestController.showEvents(contestId));
        }
    }


    public Result listContests() {
        return list(getInitialPSF(Contest.Field.TITLE, false));
    }

    /* ======================
     * PAGED TABLE for CONTESTS
     * ====================== */

    public Result list(PSF psf) {
        return ok(list_contests.render(
                getPage(dac().getContestDao().findContestsForTeachers(getLanguage()), psf, Contest.Field.class),
                new Table(psf) {
                    public Call list(PSF newPsf) {
                        return routes.TeacherContestController.list(newPsf);
                    }

                    public Call resize() {
                        return routes.TeacherContestController.resize(psf());
                    }

                    public Call action() {
                        return routes.TeacherContestController.action(psf());
                    }
                },
                this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.TeacherContestController::list);
    }

    public Result action(PSF psf) {
        // filter button - not checked
        return redirect(routes.TeacherContestController.list(
                psf.refilter(getStringMapFromForm(Contest.Field.class))
        ));
    }

}
