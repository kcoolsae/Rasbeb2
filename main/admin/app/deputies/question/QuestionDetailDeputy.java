/*
 * QuestionDetailDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.question;

import be.ugent.caagt.dao.UniqueViolation;
import be.ugent.caagt.play.util.Tab;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.AnswerType;
import be.ugent.rasbeb2.db.dto.Question;
import be.ugent.rasbeb2.db.dto.QuestionI18n;
import controllers.question.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.Result;
import views.html.question._question_tab;
import views.html.question.organiser_question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailDeputy extends OrganiserOnlyDeputy {

    @Getter
    @Setter
    @NoArgsConstructor // needed by Play / Spring
    public static class EditTranslationData {
        @Constraints.Required
        @Formats.NonEmpty
        public String title;

        @Constraints.Required
        @Formats.NonEmpty
        public String solution;

        public EditTranslationData(QuestionI18n q) {
            title = q.title();
            solution = q.solution();
        }
    }

    @Getter
    @Setter
    public static class LanguageData {
        @Constraints.Required
        @Formats.NonEmpty
        public String language;
    }

    @Getter
    @Setter
    public static class QuestionSettingsData {

        public QuestionSettingsData() {
            // needed by Play / Spring
        }

        public QuestionSettingsData(Question question) {
            this.type = question.header().answerType();
            this.externalId = question.header().externalId();
            this.typeExtra = question.typeExtra();
        }

        @Constraints.Required
        @Formats.NonEmpty
        public AnswerType type;
        public String typeExtra;
        @Constraints.Required
        @Formats.NonEmpty
        public String externalId;
    }

    public Result getQuestion(int questionId, String lang) {
        Question question = dac().getQuestionDao().getQuestion(questionId, getLanguage());
        return ok(organiser_question.render(
                getTabList(question, lang),
                formFromData(new QuestionSettingsData(question)),
                question,
                this)
        );
    }

    public Result editQuestion(int questionId) {
        Form<QuestionSettingsData> form = formFromRequest(QuestionSettingsData.class);
        QuestionDao dao = dac().getQuestionDao();
        if (form.hasErrors()) {
            error("question.settings.error");
            Question question = dao.getQuestion(questionId, getLanguage());
            return badRequest(organiser_question.render(
                    getTabList(question, ""),
                    form,
                    question,
                    this)
            );
        } else {
            QuestionSettingsData data = form.get();
            try {
                dao.editQuestion(questionId, data.type, data.typeExtra, data.externalId);
                success("question.settings.success");
            } catch (UniqueViolation ex) {
                error("question.settings.error-id"); // a non unique external id has been chosen
            }
            return redirect(routes.QuestionDetailController.getQuestion(questionId, ""));
        }
    }

    private List<Tab> getTabList(Question question, String lang) {
        if (lang.isBlank()) {
            lang = getLanguage();
        }
        boolean active = false;
        List<Tab> result = new ArrayList<>();
        List<QuestionI18n> questionI18n = dac().getQuestionDao().getQuestionI18n(question.id());
        for (QuestionI18n q : questionI18n) {
            Tab tab = new Tab(
                    q.lang(), q.lang(),
                    _question_tab.render(
                            question,
                            q,
                            formFromData(new EditTranslationData(q)),
                            this
                    )
            );
            if (q.lang().equals(lang)) {
                tab.setActive(true);
                active = true;
            }
            result.add(tab);
        }
        if (!active && !result.isEmpty()) {
            result.getFirst().setActive(true);
        }
        return result;
    }

    public Result editTranslation(int questionId, String lang) {
        Form<EditTranslationData> form = formFromRequest(EditTranslationData.class);
        QuestionDao dao = dac().getQuestionDao();
        if (form.hasErrors()) {
            Question question = dao.getQuestion(questionId, getLanguage());
            error("question.translation.edit.fail"); // TODO: use common message
            return badRequest(organiser_question.render(
                    getTabList(question, lang),
                    formFromData(new QuestionSettingsData(question)),
                    question,
                    this)
            );
        } else {
            dao.editTranslation(questionId, lang, form.get().title, form.get().solution);
            return redirect(routes.QuestionDetailController.getQuestion(questionId, lang));
        }
    }

    public Result addTranslation(int questionId) {
        Form<LanguageData> form = formFromRequest(LanguageData.class);
        QuestionDao dao = dac().getQuestionDao();
        if (!form.hasErrors()) {
            String lang = form.get().language;
            if (lang.length() == 2) {
                try {
                    dao.addTranslation(questionId, lang);
                    return redirect(routes.QuestionDetailController.getQuestion(questionId, lang));
                } catch (UniqueViolation ex) {
                    // errors fall through
                }
            }
        }
        error("question.translation.error-add");
        Question question = dao.getQuestion(questionId, getLanguage());
        return badRequest(organiser_question.render(
                getTabList(question, ""),
                formFromData(new QuestionSettingsData(question)),
                question,
                this)
        );
    }

    public Result removeTranslation(int questionId, String lang) {
        QuestionDao dao = dac().getQuestionDao();
        if (dao.questionAlreadyUploaded(questionId, lang)) {
            error("question.translation.error-remove");
        } else {
            dao.removeTranslation(questionId, lang);
            lang = "";
        }
        return redirect(controllers.question.routes.QuestionDetailController.getQuestion(questionId, lang));
    }

}
