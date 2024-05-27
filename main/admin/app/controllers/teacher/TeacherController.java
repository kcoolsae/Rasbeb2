/*
 * TeacherController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.teacher;

import controllers.TeacherOnlyController;
import deputies.teacher.TeacherDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class TeacherController  extends TeacherOnlyController<TeacherDeputy> {

    public TeacherController() {
        super(TeacherDeputy::new);
    }

    public Result getSchool(Http.Request request) {
        return createDeputy(request).getSchool();
    }

    public Result changeYear(Http.Request request) {
        return createDeputy(request).changeYear();
    }

}
