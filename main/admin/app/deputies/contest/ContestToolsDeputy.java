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
import be.ugent.rasbeb2.db.dto.AgeGroup;
import common.LanguageInfo;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;
import views.html.contest.link_list;
import views.html.contest.tools;
import views.html.contest.anomalies;
import views.html.contest.winners;

import java.util.ArrayList;
import java.util.List;

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


    /**
     * Produce a page with links to the task pages for a given contest and language
     */
    public Result listLinks(int contestId, String lang) {
        return ok(link_list.render(
                dac().getContestDao().getQuestionLinks(contestId, lang),
                lang,
                this
        ));
    }

    public record WinnerWithRank(ContestDao.Winner winner, String rank) {
    }

    public record WinnersWithAgeGroup(AgeGroup ageGroup, List<WinnerWithRank> winners) {
    }

    /**
     * Produce a page with the tables of the winners of the given contest
     */
    public Result printWinners(int contestId) {
        List<WinnersWithAgeGroup> result = new ArrayList<>();
        for (AgeGroup ageGroup : dac().getAgeGroupDao().getAgeGroups(contestId, getLanguage())) {
            List<WinnerWithRank> list = new ArrayList<>();
            int previousMarks = -1;
            int rank = 1;
            for (ContestDao.Winner winner : dac().getContestDao().getWinners(contestId, ageGroup.id(), 15)) {
                list.add(new WinnerWithRank(winner, winner.marks() == previousMarks ? "" : "" + rank));
                previousMarks = winner.marks();
                rank ++;
            }
            result.add(new WinnersWithAgeGroup(ageGroup, list));
        }
        return ok(winners.render(result, this));
    }

    /**
     * Produce the page which allows anomalies to be listed
     */
    public Result showAnomalyTools(int contestId) {
        return ok(anomalies.render(
                dac().getContestDao().getContest(contestId, getLanguage()),
                formFromData(new AnomalyData(18)),   // participations later than 18h00
                formFromData(new AnomalyData(11)),   // participations on November 11th
                null,
                this)
        );
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AnomalyData {
        public int value;
        public AnomalyData() {
            // needed by Spring
        }
    }

    public Result listAnomaliesHour(int contestId) {
        Form<AnomalyData> form = formFromRequest(AnomalyData.class);
        if (form.hasErrors()) {
            // will probably not happen
            return showAnomalyTools(contestId);
        } else {
            ContestDao dao = dac().getContestDao();
            return ok(anomalies.render(
                    dao.getContest(contestId, getLanguage()),
                    form,
                    formFromData(new AnomalyData(11)),   // participations on November 11th
                    dao.findAnomalies(contestId).listAfterHour(form.get().value),
                    this)
            );
        }
    }

    public Result listAnomaliesDay(int contestId) {
        Form<AnomalyData> form = formFromRequest(AnomalyData.class);
        if (form.hasErrors()) {
            // will probably not happen
            return showAnomalyTools(contestId);
        } else {
            ContestDao dao = dac().getContestDao();
            return ok(anomalies.render(
                    dao.getContest(contestId, getLanguage()),
                    formFromData(new AnomalyData(18)),   // participations later than 18h00
                    form,
                    dao.findAnomalies(contestId).listAtDayOfMonth(form.get().value),
                    this)
            );
        }
    }

    public Result listAnomaliesWeekend(int contestId) {
        ContestDao dao = dac().getContestDao();
        return ok(anomalies.render(
                dao.getContest(contestId, getLanguage()),
                formFromData(new AnomalyData(18)),   // participations later than 18h00
                formFromData(new AnomalyData(11)),   // participations on November 11th
                dao.findAnomalies(contestId).listInWeekend(),
                this)
        );
    }
}
