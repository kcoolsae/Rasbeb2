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
import be.ugent.rasbeb2.db.dto.*;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import play.mvc.Result;
import views.html.contest.*;

import java.util.*;

public class ContestOrderDeputy extends OrganiserOnlyDeputy {

    public Result getQuestionSet(int contestId, int ageGroupId) {
        String lang = getLanguage();
        ContestDao dao = dac().getContestDao();
        List<AgeGroup> ageGroups = dao.getAgeGroups(contestId, lang);
        if (ageGroupId == 0) {
            // default value when no id given
            ageGroupId = ageGroups.getFirst().id();
        }
        return ok(question_set_order.render(
                dao.getQuestionSet(contestId, ageGroupId, lang),
                dao.getContest(contestId, lang),
                ageGroups,
                ageGroupId,
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
