/*
 * QuestionController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.question;

import be.ugent.caagt.play.binders.PSF;
import controllers.OrganiserOnlyController;
import deputies.question.QuestionDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class QuestionController extends OrganiserOnlyController<QuestionDeputy> {
    public QuestionController() {
        super(QuestionDeputy::new);
    }

    public Result newQuestionForm(Http.Request request) {
        return createDeputy(request).newQuestionForm();
    }

    public Result newQuestion(Http.Request request) {
        return createDeputy(request).newQuestion();
    }

    public Result listQuestions (Http.Request request) {
        return createDeputy(request).listQuestions();
    }

    public Result list(Http.Request request, PSF psf) {
        return createDeputy(request).list(psf);
    }

    public Result resize(Http.Request request, PSF psf) {
        return createDeputy(request).resize(psf);
    }

    public Result action(Http.Request request, PSF psf) {
        return createDeputy(request).action(psf);
    }
}
