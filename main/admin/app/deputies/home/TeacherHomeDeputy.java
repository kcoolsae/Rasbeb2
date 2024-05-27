/*
 * TeacherHomeDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.home;

import be.ugent.rasbeb2.db.dao.ClassesDao;
import deputies.TeacherOnlyDeputy;
import play.mvc.Result;
import views.html.home.home_teacher;

public class TeacherHomeDeputy extends TeacherOnlyDeputy {

    /**
     * Renders the teacher home page. Result depends on whether the current year is active or not.
     */
    public Result index() {
        ClassesDao dao = dac().getClassesDao();
        int schoolId = dao.getSchoolId();
        int yearId = getCurrentYearId();
        return ok(home_teacher.render(
                dac().getYearDao().listAllYears(),
                dao.listClasses(schoolId, yearId),
                dac().getEventDao().listEvents(schoolId, yearId),
                this)
        );
    }
}
