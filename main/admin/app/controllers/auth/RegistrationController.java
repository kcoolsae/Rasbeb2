/*
 * RegistrationController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.auth;

import be.ugent.caagt.play.binders.PSF;
import controllers.OrganiserOnly;
import controllers.TeacherOnly;
import deputies.auth.RegistrationDeputy;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

public class RegistrationController extends EmailSendingController<RegistrationDeputy> {

    public RegistrationController() {
        super(RegistrationDeputy::new);
    }

    public Result showFirstOrganiser(Http.Request request) {
        return createDeputy(request).showFirstOrganiser();
    }

    public Result addFirstOrganiser(Http.Request request) {
        return createDeputy(request).addFirstOrganiser();
    }

    public Result firstOrganiserInfo(Http.Request request, String token) {
        return createDeputy(request).firstOrganiserInfo(token);
    }

    public Result registerFirstOrganiser(Http.Request request, String token) {
        return createDeputy(request).registerFirstOrganiser(token);
    }

    @With(OrganiserOnly.class)
    public Result organiserRegisterTeacher(Http.Request request, int schoolId) {
        return createDeputy(request).organiserRegisterTeacher(schoolId);
    }

    @With(TeacherOnly.class)
    public Result registerTeacher(Http.Request request) {
        return createDeputy(request).registerTeacher();
    }

    public Result teacherInfo(Http.Request request, String token, int schoolId) {
        return createDeputy(request).teacherInfo(token, schoolId);
    }

    public Result registerTeacherInfo(Http.Request request, String token, int schoolId) {
        return createDeputy(request).registerTeacherInfo(token, schoolId);
    }

    public Result listRegistrations (Http.Request request) {
        return createDeputy(request).listRegistrations();
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
}
