/*
 * TeacherHomeDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.home;

import deputies.TeacherOnlyDeputy;
import play.mvc.Result;
import views.html.home.home_teacher;

public class TeacherHomeDeputy extends TeacherOnlyDeputy {

    /**
     * Renders the teacher home page. Result depends on whether the current year is active or not.
     */
    public Result index() {
        int yearId = getCurrentYearId();
        return ok(home_teacher.render(
                dac().getYearDao().listAllYears(),
                dac().getClassesDao().listClasses(yearId),
                dac().getEventDao().listEvents(yearId),
                this)
        );
    }
}
