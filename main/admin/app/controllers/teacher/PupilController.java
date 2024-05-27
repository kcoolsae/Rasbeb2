/*
 * PupilController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.teacher;

import controllers.ActiveYearOnly;
import controllers.TeacherOnlyController;
import deputies.teacher.PupilDeputy;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

public class PupilController extends TeacherOnlyController<PupilDeputy> {

    public PupilController() {
        super(PupilDeputy::new);
    }

    @With(ActiveYearOnly.class)
    public Result addPupil(Http.Request request, int classId) {
        return createDeputy(request).addPupil(classId);
    }

    @With(ActiveYearOnly.class)
    public Result editPupil(Http.Request request, int pupilId) {
        return createDeputy(request).editPupil(pupilId);
    }

    @With(ActiveYearOnly.class)
    public Result removePupil(Http.Request request, int pupilId) {
        return createDeputy(request).removePupil(pupilId);
    }

    @With(ActiveYearOnly.class)
    public Result uploadPupils(Http.Request request) {
        return createDeputy(request).uploadPupils();
    }

    public Result downloadPupils(Http.Request request) {
        return createDeputy(request).downloadPupils();
    }

}
