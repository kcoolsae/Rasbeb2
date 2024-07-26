/*
 * ContestQuestionsDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.caagt.dao.Page;
import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dto.QuestionWithAgeGroups;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import util.Table;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import views.html.contest.*;

public class ContestQuestionsDeputy  extends OrganiserOnlyDeputy {

     public Result questionSelection(int contestId) {
        return listQuestions(getInitialPSF(QuestionWithAgeGroups.Field.EXTERNAL_ID), contestId);
    }

     /* ======================
     * PAGED TABLE for Questions in Contest view
     * ====================== */

    public Page<QuestionWithAgeGroups> getQuestionWithAgeGroupsPage(int contestId, String lang, PSF psf) {
        return getPage(dac().getQuestionDao().findQuestionsWithAgeGroups(contestId, lang), psf, QuestionWithAgeGroups.Field.class);
    }

    public Table getQuestionTable(PSF psf, int contestId) {
        return new Table(psf) {
            public Call list(PSF newPsf) {
                return routes.ContestQuestionsController.listQuestions(newPsf, contestId);
            }

            public Call resize() {
                return routes.ContestQuestionsController.resizeQuestions(psf(), contestId);
            }

            public Call action() {
                return routes.ContestQuestionsController.actionQuestions(psf(), contestId);
            }
        };
    }

    public Result listQuestions(PSF psf, int contestId) {
        String lang = getLanguage();
        return ok(contest_questions_select.render(
                dac().getContestDao().getContest(contestId, lang),
                getQuestionWithAgeGroupsPage(contestId, lang, psf),
                dac().getAgeGroupDao().getAgeGroups(contestId, lang),
                getQuestionTable(psf, contestId),
                this)
        );
    }

    public Result resizeQuestions(PSF psf, int contestId) {
        return resize(psf, (PSF p) -> routes.ContestQuestionsController.listQuestions(p, contestId));
    }

    @Getter
    @Setter
    public static class QuestionActionData {
        public String action;
        public List<Integer> ids;                  // which ids were present
        public Map<Integer, Map<Integer, Boolean>> checked; // which values were checked question_id -> [age_group_ids...]
    }

    public Result actionQuestions(PSF psf, int contestId) {

        Form<QuestionActionData> form = formFromRequest(QuestionActionData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            QuestionActionData data = form.get();
            if ("register".equals(data.action)) {
                if (data.checked != null) { // null when completely empty!
                    for (int questionId : data.ids) {
                        Map<Integer, Boolean> map = data.checked.get(questionId);
                        List<Integer> ageGroups = new ArrayList<>();
                        if (map != null) {
                            for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
                                if (entry.getValue()) {
                                    ageGroups.add(entry.getKey());
                                }
                            }
                        }
                        dac().getQuestionDao().setQuestionAgeGroups(contestId, questionId, ageGroups);
                    }
                }
                return redirect(routes.ContestQuestionsController.listQuestions(psf, contestId));
            } else {
                // filter button
                return redirect(routes.ContestQuestionsController.listQuestions(
                        psf.refilter(getStringMapFromForm(QuestionWithAgeGroups.Field.class)),
                        contestId
                ));
            }
        }
    }

}
