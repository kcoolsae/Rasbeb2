/*
 * ContestMarksDeputy.java
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;
import views.html.contest.*;

import java.util.*;

public class ContestMarksDeputy extends OrganiserOnlyDeputy {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarksData {
        public int marksIfCorrect;
        public int marksIfIncorrect;
    }

    @Getter
    @Setter
    public static class QuestionSetData {
        public Map<Integer, MarksData> items = new HashMap<>();
    }

    public Result getQuestionSet(int contestId, int ageGroupId) {
        String lang = getLanguage();
        List<AgeGroup> ageGroups = dac().getAgeGroupDao().getAgeGroups(contestId, lang);
        if (ageGroupId == 0) {
            // default value when no id given
            ageGroupId = ageGroups.getFirst().id();
        }

        ContestDao dao = dac().getContestDao();
        List<QuestionInSet> questionSet = dao.getQuestionSet(contestId, ageGroupId, lang);
        QuestionSetData data = new QuestionSetData();
        for (QuestionInSet q : questionSet) {
            data.items.put(q.id(), new MarksData(q.marksIfCorrect(), q.marksIfIncorrect()));
        }
        return ok(question_set_marks.render(
                formFromData(data),
                questionSet,
                dao.getContest(contestId, lang),
                ageGroups,
                ageGroupId,
                this
        ));
    }

    public Result updateMarks(int contestId, int ageGroupId) {
        Form<QuestionSetData> form = formFromRequest(QuestionSetData.class);
        ContestDao dao = dac().getContestDao();
        if (form.hasErrors()) {
            String lang = getLanguage();
            error("contest.marks.error");
            return badRequest(question_set_marks.render(
                    form,
                    dao.getQuestionSet(contestId, ageGroupId, lang),
                    dao.getContest(contestId, lang),
                    dac().getAgeGroupDao().getAgeGroups(contestId, lang),
                    ageGroupId,
                    this
            ));
        } else {
            QuestionSetData data = form.get();
            List<Integer> ids = new ArrayList<>();
            List<Integer> marksIfCorrect = new ArrayList<>();
            List<Integer> marksIfIncorrect = new ArrayList<>();
            for (Map.Entry<Integer, MarksData> entry : data.items.entrySet()) {
                ids.add(entry.getKey());
                marksIfCorrect.add(entry.getValue().marksIfCorrect);
                marksIfIncorrect.add(entry.getValue().marksIfIncorrect);
            }
            dao.updateMarks(contestId, ageGroupId, ids, marksIfCorrect, marksIfIncorrect);
            success("contest.marks.success");
            return redirect(routes.ContestController.getContest(contestId));
        }
    }

}
