/*
 * QuestionUploadController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.question;

import controllers.OrganiserOnlyController;
import deputies.question.QuestionUploadDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class QuestionUploadController extends OrganiserOnlyController<QuestionUploadDeputy> {

    public QuestionUploadController() {
        super(QuestionUploadDeputy::new);
    }

    public Result uploadPages(Http.Request request, int questionId, String lang) {
        return createDeputy(request).uploadPages(questionId, lang);
    }

    public Result uploadQuestionsForm(Http.Request request) {
        return createDeputy(request).uploadQuestionsForm();
    }

    public Result uploadQuestions(Http.Request request) {
        return createDeputy(request).uploadQuestions();
    }

}
