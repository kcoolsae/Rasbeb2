/*
 * ParticipationDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.Participation;
import be.ugent.rasbeb2.db.dto.QuestionInSet;
import be.ugent.rasbeb2.db.dto.QuestionWithAnswer;
import com.google.common.collect.Iterables;
import common.Session;
import controllers.contest.routes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;
import views.html.part.question;

import java.time.Instant;
import java.util.List;

public class ParticipationDeputy extends deputies.ContestDeputy {

    private int getContestId() {
        return Integer.parseInt(findInSession(Session.CONTEST));
    }

    private boolean inContest() {
        return isUserLoggedIn() && request.session().get(Session.CONTEST).isPresent();
    }

    private boolean deadlineHasPassed(Participation part) {
        return Instant.now().isAfter(part.deadline().plusSeconds(10));
    }

    /**
     * Displays the main participation page, containing a question for the current contest
     *
     * @param questionId id of the question to show, or zero to show the first question in the set
     */
    public Result question(int questionId) {
        if (!inContest()) {
            return redirectToIndex();
        }

        int pupilId = getPupilId();
        int contestId = getContestId();
        Participation part = dac().getParticipationDao().get(contestId, pupilId);
        if (deadlineHasPassed(part)) {
            warning("pupil.question.past-deadline");
            return redirect(routes.ParticipationController.close());
        } // TODO check whether event is already closed

        ContestDao dao = dac().getContestDao();
        List<QuestionInSet> questions = dao.getQuestionSet(contestId, part.ageGroupId(), part.lang());

        // position of question in set and of the next question
        int pos = 0;
        if (questionId != 0) {
            pos = Iterables.indexOf(questions, q -> q.id() == questionId);
            if (pos == -1) {
                // if not found (should not happen) revert to question 0
                pos = 0;
            }
        }
        int nextPos = pos + 1;
        if (nextPos == questions.size()) {
            nextPos = 0;
        }

        QuestionWithAnswer questionWithAnswer = dao.getQuestionWithAnswer(
                questions.get(pos).id(), // cannot use questionId directly when position is 0
                pupilId
        );

        return ok(question.render(
                formFromData(new AnswerData(questionWithAnswer.answer())),
                part,
                questionWithAnswer,
                questions,
                pos,
                questions.get(nextPos).id(), // id of next question after this one
                this
        ));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AnswerData {
        public String answer;

        public AnswerData(String answer) {
            this.answer = answer == null ? "" : answer;
        }
    }

    /**
     * Process the answer that was posted for the given question.
     * @param nextId Next question to show
     */
    public Result answer(int questionId, int nextId) {
        if (inContest()) {
            Form<AnswerData> form = formFromRequest(AnswerData.class);
            if (!form.hasErrors()) {
                String answer = form.get().answer.strip();
                dac().getParticipationDao().updateAnswer(getContestId(), getPupilId(), questionId, answer);
                return redirect(routes.ParticipationController.question(nextId));
            }
        }
        // errors fall through
        return badRequest();
    }

    /**
     * Show the fact that the participation has superseeded the deadline
     */
    public Result close() {
        if (!inContest()) {
            return redirectToIndex();
        }

        if (request.session().get(Session.NAME).isPresent()) {
            // normal participation - go back to the index page
            dac().getParticipationDao().close(getContestId(), getPupilId());
            return redirectToIndex().removingFromSession(request, "contest");
        } else {
            // anonymous participation directly leads to
            dac().getParticipationDao().closeAndComputeMarks(getContestId(), getPupilId());
            return redirect(controllers.contest.routes.FeedbackController.show())
                    .removingFromSession(request, Session.CONTEST)
                    .addingToSession(request, Session.FEEDBACK, String.valueOf(getContestId()));
        }
    }
}
