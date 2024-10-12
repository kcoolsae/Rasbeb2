/*
 * TeacherDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.organiser;

import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dto.TeacherWithSchool;
import be.ugent.rasbeb2.db.dto.User;
import be.ugent.rasbeb2.db.dto.Year;
import common.Session;
import deputies.OrganiserOnlyDeputy;
import lombok.AllArgsConstructor;
import util.Table;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Result;
import views.html.teacher.list_teachers;
import controllers.organiser.routes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeacherDeputy extends OrganiserOnlyDeputy {

    public Result listTeachers() {
        return list(getInitialPSF(TeacherWithSchool.Field.NAME));
    }

    public Result disableTeacher(int schoolId) {
        Form<TeacherActionData> form = formFromRequest(TeacherActionData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            TeacherActionData data = form.get();
            dac().getSchoolDao().disableTeacher(data.disable);
            return redirect(routes.SchoolController.getSchool(schoolId));
        }
    }

    public Result disableTeacherInList(int userId) {
        dac().getSchoolDao().disableTeacher(userId);
        return redirect(routes.TeacherController.listTeachers());
    }

    /* ======================
     * PAGED TABLE for TEACHERS
     * ====================== */

    public Result list(PSF psf) {
        return ok(list_teachers.render(
                getPage(dac().getSchoolDao().findTeachers(), psf, TeacherWithSchool.Field.class),
                new Table(psf) {
                    public Call list(PSF newPsf) {
                        return routes.TeacherController.list(newPsf);
                    }

                    public Call resize() {
                        return routes.TeacherController.resize(psf());
                    }

                    public Call action() {
                        return routes.TeacherController.action(psf());
                    }
                },
                this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.TeacherController::list);
    }

    @Getter
    @Setter
    public static class TeacherActionData {
        public Integer mimic;
        public Integer disable;
    }

    public Result action(PSF psf) {
        Form<TeacherActionData> form = formFromRequest(TeacherActionData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            TeacherActionData data = form.get();
            if (data.mimic != null) {
                User teacher = dac().getUserDao().getUser(data.mimic);
                int schoolId = dac().getSchoolDao().getSchoolId(teacher.id()).orElseThrow();
                return redirect(controllers.home.routes.HomeController.index()).addingToSession(
                        request,
                        Map.of(
                                Session.PARENT, getFromSession(Session.ID),
                                Session.ID, Integer.toString(teacher.id()),
                                Session.SCHOOL_ID, Integer.toString(schoolId),
                                Session.ROLE, teacher.role().name(),
                                Session.NAME, teacher.name()
                        )
                );
            } else if (data.disable == null) {
                // filter button
                return redirect(routes.TeacherController.list(
                        psf.refilter(getStringMapFromForm(TeacherWithSchool.Field.class))
                ));
            } else {
                // disable button
                return disableTeacherInList(data.disable);
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class YearIdData {
        public int yearId;

        public YearIdData() {
            // needed by Spring
        }

    }

    /**
     * Show page form which a list of email addresses can be obtained.
     */
    public Result listEmailsShow() {
        List<Year> years = dac().getYearDao().listAllYears();
        return ok (views.html.teacher.teacher_emails.render(
                years,
                formFromData(new YearIdData(years.getFirst().id())),
                this
        ));
    }

    /**
     * List the email addresses of all teachers from schools that organised one or more events in
     * a given year.
     */
    public Result listEmails() {
        Form<YearIdData> form = formFromRequest(YearIdData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            return ok(
                    dac().getSchoolDao().listTeacherEmails(form.get().yearId).stream().map(email -> email + "\n").collect(Collectors.joining())
            ).as("text/plain");
        }
    }
}
