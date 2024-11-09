/*
 * ContestDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.AgeGroup;
import be.ugent.rasbeb2.db.dto.Contest;
import be.ugent.rasbeb2.db.dto.ContestStatus;
import be.ugent.rasbeb2.db.dto.ContestType;
import common.LanguageInfo;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Result;
import util.AgeGroupsWithId;
import util.Table;
import views.html.contest.list_contests;
import views.html.contest.organiser_contest;
import views.html.contest.organiser_contest_extended;

import java.util.ArrayList;
import java.util.List;

public class ContestDeputy extends OrganiserOnlyDeputy {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor // needed by Spring
    public static class StatusData {
        public ContestStatus status;
    }

    public Result getContest(int contestId) {
        ContestDao dao = dac().getContestDao();
        Contest contest = dao.getContest(contestId, getLanguage());
        Form<StatusData> form = formFromData(new StatusData(contest.status()));
        if (contest.contestType() == ContestType.OFFICIAL && contest.isViewable()) {
            List<String> languages = dao.getContestLanguages(contestId);
            List<AgeGroup> ageGroups = dac().getAgeGroupDao().getAgeGroups(contestId, getLanguage());
            List<AgeGroupWithCounts> contents = tableContents(languages, ageGroups, dao.getCounts(contestId));
            return ok(organiser_contest_extended.render(
                    form,
                    contest,
                    LanguageInfo.list(languages),
                    contents,
                    computeTotals(contents),
                    this
            ));
        } else {
            return ok(organiser_contest.render(
                    form,
                    contest,
                    this
            ));
        }
    }

    // scala compiler cannot handle records yet
    @AllArgsConstructor
    public static class AgeGroupWithCounts {
        public String ageGroup;
        public List<Integer> counts;
    }

    private List<AgeGroupWithCounts> tableContents(
            List<String> languages, List<AgeGroup> ageGroups,
            List<ContestDao.Count> counts) {
        List<AgeGroupWithCounts> result = new ArrayList<>();
        for (AgeGroup ageGroup: ageGroups) {
            List<Integer> row = new ArrayList<>();
            int total = 0;
            for (String language : languages) {
                int pos = 0; // not overly efficient, but the lists are small
                while (pos < counts.size() &&
                       (counts.get(pos).ageGroupId() != ageGroup.id() ||
                        !counts.get(pos).lang().equals(language))) {
                    pos++;
                }
                int count = pos < counts.size() ? counts.get(pos).count() : 0;
                total += count;
                row.add(count);
            }
            row.add(total);
            result.add(new AgeGroupWithCounts(ageGroup.name(), row));
        }
        return result;
    }

    private List<Integer> computeTotals(List<AgeGroupWithCounts> awc) {
        int size = awc.getFirst().counts.size();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int total = 0;
            for (AgeGroupWithCounts agc : awc) {
                total += agc.counts.get(i);
            }
            result.add(total);
        }
        return result;
    }

    public Result updateContestStatus(int contestId) {
        dac().getContestDao().changeStatus(
                contestId, formFromRequest(StatusData.class).get().status
        );
        success("contest.settings.success-status");
        return redirect(routes.ContestController.getContest(contestId));
    }

    public Result listContests() {
        return list(getInitialPSF(Contest.Field.TITLE, false));
    }

    /* ======================
     * PAGED TABLE for CONTESTS
     * ====================== */

    public Result list(PSF psf) {
        return ok(list_contests.render(
                getPage(dac().getContestDao().findContests(getLanguage()), psf, Contest.Field.class),
                new Table(psf) {
                    public Call list(PSF newPsf) {
                        return routes.ContestController.list(newPsf);
                    }

                    public Call resize() {
                        return routes.ContestController.resize(psf());
                    }

                    public Call action() {
                        return routes.ContestController.action(psf());
                    }
                },
                this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.ContestController::list);
    }

    public Result action(PSF psf) {
        // filter button - not checked
        return redirect(routes.ContestController.list(
                psf.refilter(getStringMapFromForm(Contest.Field.class))
        ));
    }


}
