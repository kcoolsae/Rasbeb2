/*
 * ClassController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.teacher;

import controllers.ActiveYearOnly;
import controllers.TeacherOnlyController;
import deputies.teacher.ClassDeputy;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

public class ClassController extends TeacherOnlyController<ClassDeputy> {

    public ClassController() {
        super(ClassDeputy::new);
    }

    public Result getClasses(Http.Request request) {
        return createDeputy(request).getClasses();
    }

    public Result newClasses(Http.Request request) {
        return createDeputy(request).newClasses();
    }

    @With(ActiveYearOnly.class)
    public Result updateDeleteClass(Http.Request request, int classId) {
        return createDeputy(request).updateDeleteClass(classId);
    }
}
