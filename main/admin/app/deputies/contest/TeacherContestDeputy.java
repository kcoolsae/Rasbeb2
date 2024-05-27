/*
 * TeacherContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.*;
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
     *
     * @param ageGroupId age group for which the contests should be listed. Youngest age group is used if zero.
     */
    public Result listContests(int ageGroupId) {
        ContestDao dao = dac().getContestDao();
        String lang = getLanguage();
        List<AgeGroup> ageGroups = dao.getAllAgeGroups(lang);
        if (ageGroupId == 0) {
            // default value when no id given
            ageGroupId = ageGroups.getFirst().id();
        }

        return ok(views.html.contest.teacher_list_contests.render(
                ageGroupId,
                ageGroups,
                dao.getViewableContests(ageGroupId, lang),
                this
        ));
    }
    public Result getContestQuestion(int contestId, int ageGroupId, int questionId) {
        String lang = getLanguage();
        Contest c = dac().getContestDao().getContest(contestId, lang);
        if (c.contestType() == ContestType.PUBLIC && c.status() != ContestStatus.CLOSED) {
            return badRequest();
        }
        ContestWithAgeGroup contest = dac().getContestDao().getContestWithAgeGroup(contestId, ageGroupId, lang);
        List<Question> questions = dac().getQuestionDao().getQuestionsForContest(contestId, ageGroupId, lang);
        // find position of question in set
        int pos = 0;
        // if id is 0, take first question in set
        while (questionId != 0 && questions.get(pos).id() != questionId) {
            pos++;
        }
        return ok(teacher_contest.render(contest, lang, questions, pos, this));
    }

}
