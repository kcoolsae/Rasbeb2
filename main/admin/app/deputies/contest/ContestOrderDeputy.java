/*
 * ContestOrderDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;


import be.ugent.rasbeb2.db.dao.ContestDao;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import play.mvc.Result;
import util.AgeGroupsWithId;
import views.html.contest.*;

public class ContestOrderDeputy extends OrganiserOnlyDeputy {

    public Result getQuestionSet(int contestId, int ageGroupId) {
        String lang = getLanguage();
        AgeGroupsWithId ageGroups = new AgeGroupsWithId(
                dac().getAgeGroupDao().getAllAgeGroups(lang),
                ageGroupId
        );
        ContestDao dao = dac().getContestDao();
        return ok(question_set_order.render(
                dao.getQuestionSet(contestId, ageGroups.id(), lang),
                dao.getContest(contestId, lang),
                ageGroups,
                this
        ));
    }

    public Result updateOrderByDifficulty(int contestId, int ageGroupId) {
        dac().getContestDao().updateOrder(contestId, ageGroupId);
        success("contest.order.success");
        return redirect(routes.ContestOrderController.getQuestionSet(contestId, ageGroupId));
    }

    public Result updateOrder(int contestId, int ageGroupId, int seqNum1, int seqNum2) {
        dac().getContestDao().updateOrder(contestId, ageGroupId, seqNum1, seqNum2);
        return redirect(routes.ContestOrderController.getQuestionSet(contestId, ageGroupId));
    }

}
