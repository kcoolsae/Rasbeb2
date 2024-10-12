/*
 * TeacherController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.organiser;

import be.ugent.caagt.play.binders.PSF;
import controllers.OrganiserOnlyController;
import deputies.organiser.TeacherDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class TeacherController extends OrganiserOnlyController<TeacherDeputy> {

    public TeacherController() {
        super(TeacherDeputy::new);
    }

    public Result listTeachers (Http.Request request) {
        return createDeputy(request).listTeachers();
    }

    public Result disableTeacher(Http.Request request, int schoolId) {
        return createDeputy(request).disableTeacher(schoolId);
    }

    public Result disableTeacherInList(Http.Request request, int userId) {
        return createDeputy(request).disableTeacherInList(userId);
    }

    public Result list(Http.Request request, PSF psf) {
        return createDeputy(request).list(psf);
    }

    public Result resize(Http.Request request, PSF psf) {
        return createDeputy(request).resize(psf);
    }

    public Result action(Http.Request request, PSF psf) {
        return createDeputy(request).action(psf);
    }

    public Result listEmailsShow(Http.Request request) {
        return createDeputy(request).listEmailsShow();
    }

    public Result listEmails(Http.Request request) {
        return createDeputy(request).listEmails();
    }

}
