/*
 * FeedbackDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dto.ParticipationWithMarks;
import be.ugent.rasbeb2.db.dto.QuestionWithFeedback;
import com.google.common.collect.Iterables;
import common.Session;
import deputies.ContestDeputy;
import play.mvc.Result;
import views.html.feedback.feedback_question;
import views.html.feedback.feedback_show_anon;
import views.html.feedback.feedback_show_pupil;

import java.util.List;

public class FeedbackDeputy extends ContestDeputy {

    protected boolean inFeedback() {
        return isUserLoggedIn() && request.session().get(Session.FEEDBACK).isPresent();
    }

    protected int getContestId() {
        return Integer.parseInt(findInSession(Session.FEEDBACK));
    }

    /**
     * Show the list of questions for feedback
     */
    public Result show() {
        if (inFeedback()) {
            ParticipationDao dao = dac().getParticipationDao();
            ParticipationWithMarks par = dao.getMarks(getContestId(), getPupilId());
            List<QuestionWithFeedback> questions = dao.getQuestionMarks(par.contestId(), par.pupilId(), par.ageGroupId(), par.lang());
            if (pupilLoggedIn()) {
                return ok(feedback_show_pupil.render(par, questions, this));
            } else {
                return ok(feedback_show_anon.render(par, questions, this));
            }
        } else {
            return redirectToIndex();
        }
    }

    /**
     * Show the feedback page for a specific question
     */
    public Result question(int questionId) {
        if (inFeedback()) {
            ParticipationDao dao = dac().getParticipationDao();
            ParticipationWithMarks par = dao.getMarks(getContestId(), getPupilId());
            List<QuestionWithFeedback> questions = dao.getQuestionMarks(par.contestId(), par.pupilId(), par.ageGroupId(), par.lang());
            // position of question in set
            int pos = Iterables.indexOf(questions, q -> q.id() == questionId);
            return ok(feedback_question.render(par, questions, pos, this));
        } else {
            return redirectToIndex();
        }
    }

    /**
     * Terminates feedback status after button was clicked
     */
    public Result close() {
        if (inFeedback()) {
            if (request.session().get(Session.NAME).isPresent()) { // pupil logged in
                return redirectToIndex()
                        .removingFromSession(request, Session.CONTEST)
                        .removingFromSession(request, Session.FEEDBACK);
            } else { // anon logged in
                return redirect(controllers.auth.routes.AuthenticationController.login())
                        .withNewSession();
            }
        }
        // error drops through
        return badRequest();
    }
}
