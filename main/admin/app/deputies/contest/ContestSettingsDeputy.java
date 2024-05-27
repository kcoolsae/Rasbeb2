/*
 * ContestSettingsDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.contest;

import be.ugent.caagt.dao.ForeignKeyViolation;
import be.ugent.rasbeb2.db.dao.ContestDao;
import be.ugent.rasbeb2.db.dto.*;
import controllers.contest.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;
import views.html.contest.contest_settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContestSettingsDeputy extends OrganiserOnlyDeputy {

    @Getter
    @Setter
    @NoArgsConstructor // needed by Spring
    public static class ContestData {
        public ContestType type;
        public Map<String, String> title; // language -> title

        public ContestData(ContestType type, List<ContestI18n> info) {
            this.type = type;
            this.title = info.stream().collect(Collectors.toMap(
                    ContestI18n::lang, ContestI18n::title
            ));
        }
    }

    public Result newContestForm() {
        return ok(views.html.contest.new_contest.render(
                emptyForm(ContestData.class),
                this
        ));
    }

    public Result newContest() {
        Form<ContestData> form = formFromRequest(ContestData.class);
        ContestData data = form.get();
        if (data.title.values().stream().allMatch(String::isBlank)) {
            error("contest.error-empty");
            return badRequest(views.html.contest.new_contest.render(
                    form,
                    this
            ));
        } else {
            dac().getContestDao().addContest(
                    data.type, data.title, getConfig().getInt("rasbeb2.default-duration")
            );
            return redirect(routes.ContestController.listContests());
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DurationData {
        public Map<Integer,Integer> duration;  // age_group_id -> duration (or null)

        public DurationData (Iterable<AgeGroupWithDuration> list) {
            duration = new HashMap<>();
            for (AgeGroupWithDuration ageGroup : list) {
                duration.put(ageGroup.ageGroup().id(), ageGroup.duration());
            }
        }
    }

    public Result settingsForm(int contestId) {
        String lang = getLanguage();
        ContestDao dao = dac().getContestDao();
        Contest contest = dao.getContest(contestId, lang);
        List<ContestI18n> list = dao.getAllContestTranslations(contestId);
        List<AgeGroupWithDuration> ageGroups = dao.getAgeGroupsWithDuration(contestId, lang);
        return ok(contest_settings.render(
                formFromData(new ContestData(contest.contestType(), list)),
                formFromData(new DurationData(ageGroups)),
                contest,
                list.stream().map(ContestI18n::lang).toList(),
                ageGroups,
                this
        ));
    }

    public Result titleSettings(int contestId) {
        Form<ContestData> form = formFromRequest(ContestData.class);
        ContestData data = form.get();
        ContestDao dao = dac().getContestDao();
        try {
            // first do all updates and deletes
            for (Map.Entry<String, String> entry : data.title.entrySet()) {
                String value = entry.getValue();
                String lang = entry.getKey();

                if (value.isBlank()) {
                    dao.removeContestLanguage(contestId, lang);
                } else {
                    dao.updateContestI18n(contestId, lang, value);
                }
            }

            // were additional languages specified?
            processLanguages(contestId);
        } catch (ForeignKeyViolation ex) {
            error("contest.error-delete-lang");
        }
        return redirect(routes.ContestSettingsController.settingsForm(contestId));
    }

    public Result ageGroupSettings(int contestId) {
        Form<DurationData> form = formFromRequest(DurationData.class);
        DurationData data = form.get(); // note that
        ContestDao dao = dac().getContestDao();
        try {
            for (Map.Entry<Integer, Integer> entry : data.duration.entrySet()) {
                int ageGroupId = entry.getKey();
                Integer duration = entry.getValue();
                if (duration == null || duration == 0) {
                    dao.removeAgeGroup(contestId, ageGroupId);
                } else {
                    dao.updateDuration(contestId, ageGroupId, duration);
                }
            }
        } catch (ForeignKeyViolation ex) {
            error("contest.error-delete-ag");
        }
        return redirect(routes.ContestSettingsController.settingsForm(contestId));
    }

    @Getter
    @Setter
    public static class LanguagesData {
        public String languages;
    }

    private void processLanguages(int contestId) {
        dac().getContestDao().addContestLanguages(
                contestId,
                formFromRequest(LanguagesData.class).get().languages
        );
    }



}
