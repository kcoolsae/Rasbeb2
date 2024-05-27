/*
 * QuestionDetailController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.question;

import controllers.OrganiserOnlyController;
import deputies.question.QuestionDetailDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class QuestionDetailController extends OrganiserOnlyController<QuestionDetailDeputy> {

    public QuestionDetailController() {
        super(QuestionDetailDeputy::new);
    }

    public Result getQuestion(Http.Request request, int questionId, String lang) {
        return createDeputy(request).getQuestion(questionId, lang);
    }

    public Result editQuestion(Http.Request request, int questionId) {
        return createDeputy(request).editQuestion(questionId);
    }

    public Result editTranslation(Http.Request request, int questionId, String lang) {
        return createDeputy(request).editTranslation(questionId, lang);
    }

    public Result addTranslation(Http.Request request, int questionId) {
        return createDeputy(request).addTranslation(questionId);
    }

    public Result removeTranslation(Http.Request request, int questionId, String lang) {
        return createDeputy(request).removeTranslation(questionId, lang);
    }

}
