/*
 * ContestToolsDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.rasbeb2.db.dao.ContestDao;
import common.LanguageInfo;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import play.mvc.Result;
import views.html.contest.link_list;
import views.html.contest.tools;

public class ContestToolsDeputy extends OrganiserOnlyDeputy {

    /**
     * Shows additional tools that can be used on a contest. Currently organiser only
     */
    public Result tools(int contestId) {
        if (isOrganiser()) {

            ContestDao dao = dac().getContestDao();
            return ok(tools.render(
                    dao.getContest(contestId, getLanguage()),
                    LanguageInfo.list(dao.getContestLanguages(contestId)),
                    this
            ));
        } else {
            return badRequest();
        }
    }

    /**
     * Make a copy of a contest
     */
    public Result copyContest(int contestId) {
        int newId = dac().getContestDao().copyContest(contestId);
        success("contest.copy-contest.message");
        return redirect(routes.ContestSettingsController.settingsForm(newId));
    }

    public Result listLinks(int contestId, String lang) {
        return ok(link_list.render(
                dac().getContestDao().getQuestionLinks(contestId, lang),
                lang,
                this
        ));
    }

}
