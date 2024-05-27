/*
 * SchoolController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.organiser;

import be.ugent.caagt.play.binders.PSF;
import controllers.OrganiserOnlyController;
import deputies.organiser.SchoolDeputy;
import play.mvc.Http;
import play.mvc.Result;

public class SchoolController extends OrganiserOnlyController<SchoolDeputy> {

    public SchoolController() {
        super(SchoolDeputy::new);
    }

    public Result newSchool (Http.Request request) {
        return createDeputy(request).newSchool();
    }

    public Result editSchool (Http.Request request, int schoolId) {
        return createDeputy(request).editSchool(schoolId);
    }

    public Result removeSchool (Http.Request request, int schoolId) {
        return createDeputy(request).removeSchool(schoolId);
    }

    public Result listSchools (Http.Request request) {
        return createDeputy(request).listSchools();
    }

    public Result getSchool (Http.Request request, int schoolId) {
        return createDeputy(request).getSchool(schoolId);
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
