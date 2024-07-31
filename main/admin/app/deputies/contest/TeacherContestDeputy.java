/*
 * TeacherContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dao.QuestionDao;
import be.ugent.rasbeb2.db.dto.*;
import common.LanguageInfo;
import deputies.TeacherOnlyDeputy;
import play.mvc.Result;
import views.html.contest.teacher_contest;

import java.util.List;

/**
 * Provides views of questions and feedback for teachers.
 */
public class TeacherContestDeputy extends TeacherOnlyDeputy {

    /**
     * List all contests that can be viewed by a teacher.
     * @param language for which the contests should be listed
     * @param ageGroupId age group for which the contests should be listed. Youngest age group is used if zero.
     */
    public Result listContests(String language, int ageGroupId) {
        // TODO much in common with EventDeputy.listEventContests
        List<AgeGroup> ageGroups = dac().getAgeGroupDao().getAllAgeGroups(language);
        if (ageGroupId == 0) {
            // default value when no id given
            ageGroupId = ageGroups.getFirst().id();
        }

        return ok(views.html.contest.teacher_list_contests.render(
                LanguageInfo.get(language),
                getUILanguagesInfo(),
                ageGroupId,
                ageGroups,
                dac().getContestDao().getViewableContests(ageGroupId, language),
                this
        ));
    }

    public Result getContestQuestion(String lang, int contestId, int ageGroupId, int questionId) {
        ContestWithAgeGroup cwa = dac().getPupilContestDao().getContestWithAgeGroup(contestId, ageGroupId, lang);
        QuestionDao questionDao = dac().getQuestionDao();
        List<QuestionHeader> headers = questionDao.getQuestionsForContest(contestId, ageGroupId, lang);
        if (questionId == 0) {
            questionId = headers.getFirst().id();
        }
        Question question = questionDao.getQuestion(questionId, lang);
        Contest contest = cwa.contest();
        boolean showFeedback = contest.contestType() != ContestType.OFFICIAL || contest.status() == ContestStatus.CLOSED;
        return ok(teacher_contest.render(cwa, lang, question, headers, showFeedback, this));
    }

}
