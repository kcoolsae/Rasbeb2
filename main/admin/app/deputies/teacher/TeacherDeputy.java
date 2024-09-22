/*
 * TeacherDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.teacher;

import be.ugent.rasbeb2.db.dao.SchoolDao;
import be.ugent.rasbeb2.db.dto.School;
import be.ugent.rasbeb2.db.dto.Year;
import common.Session;
import deputies.TeacherOnlyDeputy;
import play.mvc.Result;
import views.html.school.teacher_school;

public class TeacherDeputy extends TeacherOnlyDeputy {

    public Result getSchool() {
        SchoolDao dao = dac().getSchoolDao();
        School school = dao.getSchool();
        int schoolId = school.id();
        return ok(teacher_school.render(
                school,
                getCurrentYear(),
                dao.listAllTeachers(schoolId, false),
                this
        ));
    }

     public Result changeYear() {
        int yearId = Integer.parseInt(mapFromRequest().get("year"));
        Year year = dac().getYearDao().getYear(yearId);
        return redirect(controllers.home.routes.TeacherHomeController.index())
                .addingToSession(request, Session.YEAR, String.valueOf(yearId))
                .addingToSession(request, Session.YEAR_NAME, year.name())
                .addingToSession(request, Session.YEAR_ACTIVE, Boolean.toString(dac().getYearDao().getRecentYear().id() == year.id())); // TODO can this be shortened?
    }


}
