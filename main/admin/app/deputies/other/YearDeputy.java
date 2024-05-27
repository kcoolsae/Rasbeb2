/*
 * YearDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.other;

import be.ugent.rasbeb2.db.dao.YearDao;
import controllers.other.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;
import views.html.other.list_years;

public class YearDeputy extends OrganiserOnlyDeputy {
    @Getter
    @Setter
    public static class YearData {
        public String yearName;
    }

    public Result newYear() {
        Form<YearDeputy.YearData> form = formFromRequest(YearDeputy.YearData.class);
        YearDao dao = dac().getYearDao();
        if (form.hasErrors()) {
            return badRequest(); // this should not happen
        } else {
            dao.createYear(form.get().yearName);
            success("year.added");
            return redirect(routes.YearController.listYears());
        }
    }

    public Result listYears() {
        return ok(list_years.render(
                dac().getYearDao().listAllYears(),
                emptyForm(YearData.class),
                this
        ));
    }

    @Getter
    @Setter
    public static class UpdateDeleteData {
        // validation must be done in the deputy method
        public String yearName;
        public String action;
    }

    public Result updateDeleteYear(int yearId) {
        Form<UpdateDeleteData> form = formFromRequest(UpdateDeleteData.class);
        // form should not have errors
        if (form.hasErrors()) {
            return badRequest(); // this should not happen
        } else {
            UpdateDeleteData data = form.get();
            if ("delete".equals(data.action)) {
                deleteYear(yearId);
            } else {
                updateYear(yearId, data.yearName);
            }
            return redirect(routes.YearController.listYears());
        }
    }

    private void deleteYear(int yearId) {
        try {
            dac().getYearDao().removeYear(yearId);
        } catch (Exception ignored) {
            error("year.error.linked");
        }
    }

    private void updateYear(int yearId, String yearName) {
        if (yearName == null || yearName.isBlank()) {
            // this should not happen
            error("year.error.required");
        } else {
            dac().getYearDao().updateYear(yearId, yearName);
        }
    }
}
