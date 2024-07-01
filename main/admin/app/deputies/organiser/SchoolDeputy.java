/*
 * SchoolDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.organiser;

import be.ugent.caagt.dao.DataAccessException;
import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.School;
import controllers.organiser.routes;
import deputies.OrganiserOnlyDeputy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Call;
import play.mvc.Result;
import util.Table;
import views.html.school.list_schools;
import views.html.school.organiser_school;

public class SchoolDeputy extends OrganiserOnlyDeputy {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SchoolData {

        public SchoolData(School school) {
            this.name = school.name();
            this.street = school.street();
            this.zip = school.zip();
            this.town = school.town();
        }

        @Constraints.Required
        public String name;

        public String street;

        public String zip;

        @Constraints.Required
        public String town;

    }

    public Result newSchool() {
        Form<SchoolData> form = formFromRequest(SchoolData.class);
        if (form.hasErrors()) {
            return listWithErrors(getInitialPSF(School.Field.NAME), form);
        } else {
            SchoolData data = form.get();
            dac().getClassesDao().createSchool(
                    data.name, data.street, data.zip, data.town
            );
            success("school.schools.success-added");
            return redirect(routes.SchoolController.listSchools());
        }
    }

    public Result editSchool(int schoolId) {
        Form<SchoolData> form = formFromRequest(SchoolData.class);
        ClassesDao dao = dac().getClassesDao();
        if (form.hasErrors()) {
            return badRequest(organiser_school.render(
                    dao.getSchool(schoolId),
                    dao.listAllTeachers(schoolId),
                    form,
                    "school",
                    this));
        } else {
            SchoolData data = form.get();
            dao.editSchool(
                    schoolId, data.name, data.street, data.zip, data.town
            );
            success("school.schools.success-updated");
            return redirect(routes.SchoolController.getSchool(schoolId));
        }
    }

    public Result removeSchool(int schoolId) {
        try {
            dac().getClassesDao().removeSchool(schoolId);
            success("school.schools.success-deleted");
        } catch (DataAccessException ex) {
            error("school.schools.error-deleted");
        }
        return redirect(routes.SchoolController.listSchools());
    }

    public Result listSchools() {
        return list(getInitialPSF(School.Field.NAME));
    }

    public Result getSchool(int schoolId) {
        ClassesDao dao = dac().getClassesDao();
        School school = dao.getSchool(schoolId);
        return ok(organiser_school.render(
                school,
                dao.listAllTeachers(schoolId),
                formFromData(new SchoolData(school)),
                "",
                this
        ));
    }

    /* ======================
     * PAGED TABLE for SCHOOLS
     * ====================== */

    public Table getTable(PSF psf) {
        return new Table(psf) {
            public Call list(PSF newPsf) {
                return routes.SchoolController.list(newPsf);
            }

            public Call resize() {
                return routes.SchoolController.resize(psf());
            }

            public Call action() {
                return routes.SchoolController.action(psf());
            }
        };
    }

    public Result list(PSF psf) {
        return ok(list_schools.render(
                getPage(dac().getClassesDao().findSchools(), psf, School.Field.class),
                emptyForm(SchoolData.class), "", getTable(psf), this)
        );
    }

    public Result listWithErrors(PSF psf, Form<SchoolData> form) {
        return badRequest(list_schools.render(
                getPage(dac().getClassesDao().findSchools(), psf, School.Field.class),
                form, "new", getTable(psf), this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.SchoolController::list);
    }

    @Getter
    @Setter
    public static class SchoolActionData {
        public Integer delete;
    }

    public Result action(PSF psf) {
        Form<SchoolActionData> form = formFromRequest(SchoolActionData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            SchoolActionData data = form.get();
            if (data.delete == null) {
                // filter button
                return redirect(routes.SchoolController.list(
                        psf.refilter(getStringMapFromForm(School.Field.class))
                ));
            } else {
                // delete button
                return removeSchool(data.delete);
            }
        }
    }

}
