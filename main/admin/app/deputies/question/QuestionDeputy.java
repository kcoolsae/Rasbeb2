/*
 * QuestionDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.question;

import be.ugent.caagt.dao.UniqueViolation;
import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.AnswerType;
import be.ugent.rasbeb2.db.dto.QuestionHeader;
import be.ugent.rasbeb2.db.dto.Translation;
import controllers.question.routes;
import deputies.OrganiserOnlyDeputy;
import util.Table;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Call;
import play.mvc.Result;
import views.html.question.list_questions;
import views.html.question.new_question;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class QuestionDeputy extends OrganiserOnlyDeputy {


    @Getter
    @Setter
    @Constraints.Validate
    public static class QuestionI18nData implements Constraints.Validatable<ValidationError> {

        public String lang;
        public String title;
        public String solution;

        public ValidationError validate() {
            if (title.isBlank()) {
                if (solution.isBlank()) {
                    return null; // ignore language
                } else {
                    return new ValidationError("title", "error.required");
                }
            } else if (lang.isBlank()) {
                return new ValidationError("lang", "error.required");
            } else {
                return null;
            }
        }

    }

    @Getter
    @Setter
    public static class QuestionData {
        @Constraints.Required
        @Formats.NonEmpty
        public AnswerType type;

        public String typeExtra;

        @Constraints.Required
        @Formats.NonEmpty
        public String externalId;

        @Valid
        public List<QuestionI18nData> items = new ArrayList<>();
    }


    /**
     * Creates data reflecting the most common use case
     */
    private QuestionData initialQuestionData() {
        QuestionData result = new QuestionData();
        result.type = AnswerType.MC;
        result.typeExtra = "4";
        result.externalId = "";

        for (String lang : getUILanguages()) {
            QuestionI18nData q = new QuestionI18nData();
            q.lang = lang;
            result.items.add(q);
        }
        return result;
    }

    public Result newQuestionForm() {
        QuestionData data = initialQuestionData();
        return ok(new_question.render(
                formFromData(data),
                data.items.size(),
                this
        ));
    }

    public Result newQuestion() {
        Form<QuestionData> form = formFromRequest(QuestionData.class);
        if (!form.hasErrors()) {
            QuestionData data = form.get();
            try {
                // create question proper
                QuestionDao dao = dac().getQuestionDao();
                int questionId = dao.createQuestion(data.type, data.typeExtra, data.externalId);
                // create translations
                for (QuestionI18nData item : data.items) {
                    if (!item.title.isBlank() && !item.lang.isBlank()) {
                        dao.addTranslation(questionId, new Translation(item.lang, item.title, item.solution));
                    }
                }
                return redirect(routes.QuestionController.listQuestions());
            } catch (UniqueViolation ex) {
                error("question.not-unique");
            }
        }
        // errors fall through
        return badRequest(new_question.render(
                form,
                getUILanguages().size(),
                this
        ));
    }

    public Result listQuestions() {
        return list(getInitialPSF(QuestionHeader.Field.ID));
    }

    /* ======================
     * PAGED TABLE for Questions
     * ====================== */

    public Result list(PSF psf) {
        return ok(list_questions.render(
                emptyForm(QuestionUploadDeputy.LanguageData.class), // TODO
                getPage(dac().getQuestionDao().findQuestions(getLanguage()), psf, QuestionHeader.Field.class),
                new Table(psf) {
                    public Call list(PSF newPsf) {
                        return routes.QuestionController.list(newPsf);
                    }

                    public Call resize() {
                        return routes.QuestionController.resize(psf());
                    }

                    public Call action() {
                        return routes.QuestionController.action(psf());
                    }
                },
                this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.QuestionController::list);
    }

    @Getter
    @Setter
    public static class QuestionActionData {
        public Integer disable;
    }

    public Result action(PSF psf) {
        Form<QuestionActionData> form = formFromRequest(QuestionActionData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            QuestionActionData data = form.get();
            if (data.disable == null) {
                // filter button
                return redirect(routes.QuestionController.list(
                        psf.refilter(getStringMapFromForm(QuestionHeader.Field.class))
                ));
            } else {
                // disable button
                return badRequest("TODO"); // TODO
            }
        }
    }

}
