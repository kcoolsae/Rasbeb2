/*
 * ParticipationDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.Participation;
import be.ugent.rasbeb2.db.dto.QuestionHeader;
import be.ugent.rasbeb2.db.dto.QuestionInContest;
import common.Session;
import controllers.contest.routes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;

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
        ParticipationDao participationDao = dac().getParticipationDao();
        Participation part = participationDao.get(contestId, pupilId);
        if (deadlineHasPassed(part)) {
            warning("pupil.question.past-deadline");
            LOGGER.info("{} {} past deadline", pupilId, contestId);
            return redirect(routes.ParticipationController.close());
        } // TODO check whether event is already closed

        QuestionDao questionDao = dac().getQuestionDao();
        List<QuestionHeader> headers = questionDao.getQuestionsForContest(
                contestId, part.ageGroupId(), part.lang()
        );

        // position of question in set and of the next question
        int pos = 0;
        if (questionId == 0) {
            questionId = headers.getFirst().id();
        } else {
            while (pos < headers.size() && headers.get(pos).id() != questionId) {
                pos++;
            }
            if (pos == headers.size()) { // if not found (should not happen) revert to question 0
                pos = 0;
            }
        }
        int nextPos = pos + 1;
        if (nextPos == headers.size()) {
            nextPos = 0;
        }

        QuestionInContest question = questionDao.getQuestionInContest(
                contestId, questionId, part.ageGroupId(), part.lang()
        );
        LOGGER.info("{} {} {} view question", pupilId, contestId, questionId);
        return ok(views.html.part.question.render(
                formFromData(new AnswerData(
                        participationDao.getAnswer(contestId, pupilId, questionId)
                )),
                part,
                question,
                headers,
                headers.get(nextPos).id(), // id of next question after this one
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
                int pupilId = getPupilId();
                int contestId = getContestId();
                dac().getParticipationDao().updateAnswer(contestId, pupilId, questionId, answer);
                LOGGER.info("{} {} {} answered: {} ", pupilId, contestId, questionId, answer);
                return redirect(routes.ParticipationController.question(nextId));
            }
        }
        // errors fall through
        return badRequest();
    }

    /**
     * Show the fact that the participation has superceeded the deadline
     */
    public Result close() {
        if (!inContest()) {
            return redirectToIndex();
        }

        int contestId = getContestId();
        int pupilId = getPupilId();
        if (request.session().get(Session.NAME).isPresent()) {
            // normal participation - go back to the index page
            dac().getParticipationDao().close(contestId, pupilId);
            LOGGER.info("{} {} closed participation", pupilId, contestId);
            return redirectToIndex().removingFromSession(request, "contest");
        } else {
            // anonymous participation directly leads to
            dac().getParticipationDao().closeAndComputeMarks(contestId, pupilId);
            return redirect(controllers.contest.routes.FeedbackController.show())
                    .removingFromSession(request, Session.CONTEST)
                    .addingToSession(request, Session.FEEDBACK, String.valueOf(contestId));
        }
    }
}
