/*
 * RegistrationDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.auth;

import be.ugent.caagt.play.binders.PSF;
import be.ugent.rasbeb2.db.dao.RegistrationDao;
import be.ugent.rasbeb2.db.dao.UserDao;
import be.ugent.rasbeb2.db.dto.Registration;
import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.dto.User;
import controllers.auth.routes;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.Call;
import play.mvc.Result;
import util.Table;
import views.html.auth.list_registrations;
import views.html.auth.registrationInfo;

public class RegistrationDeputy extends EmailSendingDeputy {

    @Getter
    @Setter
    public static class RegistrationData extends PasswordDeputy.NewPasswordData {
        @Constraints.Required
        @Formats.NonEmpty
        public String name;
    }

    public Result showFirstOrganiser() {
        return ok(views.html.organiser.init.render(this));
    }

    private boolean firstOrganiserNotNeeded() {
        return !dac().getOrganiserDao().noOrganisersRegistered();
    }

    public Result addFirstOrganiser() {
        if (firstOrganiserNotNeeded()) {
            return badRequest();
        }
        Form<EmailData> form = formFromRequest(EmailData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            String token = dac().getRegistrationDao().addPasswordRequest(form.get().email);
            sendEmail(
                    i18n("init.mail.subject"),
                    form.get().email,
                    i18n("init.mail.text",
                            hostUri() +
                                    routes.RegistrationController.firstOrganiserInfo(token)
                    )
            );
            success("init.message");
            return redirect(routes.RegistrationController.addFirstOrganiser());
        }
    }

    public Result firstOrganiserInfo(String token) {
        return ok(registrationInfo.render(
                routes.RegistrationController.registerFirstOrganiser(token),
                emptyForm(RegistrationData.class), token, this
        ));
    }

    public Result registerFirstOrganiser(String token) {
        if (firstOrganiserNotNeeded()) {
            return badRequest();
        }
        Form<RegistrationData> form = formFromRequest(RegistrationData.class);
        if (form.hasErrors()) {
            return badRequest(registrationInfo.render(
                    routes.RegistrationController.registerFirstOrganiser(token),
                    form, token, this
            ));
        } else {
            RegistrationData data = form.get();
            RegistrationDao registrationDao = dac().getRegistrationDao();
            if (registrationDao.isValidPasswordRequest(data.email, token)) {
                // create user
                UserDao userDao = dac().getUserDao();
                int userId = userDao.createUser(data.name, data.email.toLowerCase().strip(), Role.ORGANISER);
                userDao.updatePassword(userId, data.password);
                // delete token
                registrationDao.deletePasswordRequest(data.email);
                success("auth.registration.success");
                return redirect(controllers.home.routes.HomeController.index()).withNewSession();
            } else {
                error("auth.registration.error");
                return badRequest(registrationInfo.render(
                        routes.RegistrationController.registerFirstOrganiser(token),
                        form, token, this
                ));
            }
        }
    }

    private void sendRegistrationMail(int schoolId, String email) {
        String token = dac().getRegistrationDao().addRegistration(email, schoolId);
        sendEmail(
                i18n("auth.registration-request.mail.subject"),
                email,
                i18n("auth.registration-request.mail.text",
                        hostUri() + routes.RegistrationController.teacherInfo(token, schoolId)
                )
        );
    }

    /**
     * Organiser registers teacher of the given school
     */
    public Result organiserRegisterTeacher(int schoolId) {
        Form<EmailData> form = formFromRequest(EmailData.class);
        if (form.hasErrors()) {
            // when e-mailaddress is invalid
            error("auth.registration-request.error");
            // falls through
        } else {
            String email = form.get().email;
            if (dac().getUserDao().isKnownEmailAddress(email)) {
                error("auth.registration-request.error-exists");
                // falls through
            } else {
                sendRegistrationMail(schoolId, email);
                success("auth.registration-request.message");
                return redirect(routes.RegistrationController.listRegistrations());
            }
        }
        return redirect(controllers.organiser.routes.SchoolController.getSchool(schoolId));
    }

    /**
     * Teacher registers teacher of the same school
     */
    public Result registerTeacher() {
        Form<EmailData> form = formFromRequest(EmailData.class);
        if (form.hasErrors()) {
            // when e-mailaddress is invalid
            error("auth.registration-request.error");
        } else {
            int schoolId = getCurrentSchoolId();
            String email = form.get().email;
            User user = dac().getUserDao().findUserByEmail(email);
            if (user == null) {
                sendRegistrationMail(schoolId, email);
            } else {
                sendEmail(
                        i18n("auth.registration-request.mail-exists.subject"),
                        email,
                        i18n("auth.registration-request.mail-exists.text", request.remoteAddress())
                );
            }
            success("auth.registration-request.message");
        }
        return redirect(controllers.teacher.routes.TeacherController.getSchool());
    }

    public Result teacherInfo(String token, int schoolId) {
        return ok(registrationInfo.render(
                routes.RegistrationController.registerTeacherInfo(token, schoolId),
                emptyForm(RegistrationData.class), token, this
        ));
    }

    public Result registerTeacherInfo(String token, int schoolId) {
        Form<RegistrationData> form = formFromRequest(RegistrationData.class);
        if (form.hasErrors()) {
            return badRequest(registrationInfo.render(
                    routes.RegistrationController.registerTeacherInfo(token, schoolId),
                    form, token, this
            ));
        } else {
            RegistrationData data = form.get();
            RegistrationDao registrationDao = dac().getRegistrationDao();
            if (registrationDao.isValidRegistration(data.email, token, schoolId)) {
                // create user
                UserDao userDao = dac().getUserDao();
                // TODO combine this into a single DAO method?
                int userId = userDao.createUser(data.name, data.email.toLowerCase().strip(), Role.TEACHER);
                userDao.updatePassword(userId, data.password);
                // delete token
                registrationDao.deleteRegistration(data.email);
                // add link to school
                userDao.createTeacher(userId, schoolId);
                success("auth.registration.success");
                return redirect(controllers.home.routes.HomeController.index()).withNewSession();
            } else {
                error("auth.registration.error");
                return badRequest(registrationInfo.render(
                        routes.RegistrationController.registerTeacherInfo(token, schoolId),
                        form, token, this
                ));
            }
        }
    }


    public Result listRegistrations() {
        return list(getInitialPSF(Registration.Field.EMAIL));
    }

    /* ======================
     * PAGED TABLE for REGISTRATIONS
     * ====================== */

    public Result list(PSF psf) {
        return ok(list_registrations.render(
                getPage(dac().getRegistrationDao().findRegistrations(), psf, Registration.Field.class),
                new Table(psf) {
                    public Call list(PSF newPsf) {
                        return routes.RegistrationController.list(newPsf);
                    }

                    public Call resize() {
                        return routes.RegistrationController.resize(psf());
                    }

                    public Call action() {
                        return routes.RegistrationController.action(psf());
                    }
                },
                this)
        );
    }

    public Result resize(PSF psf) {
        return resize(psf, routes.RegistrationController::list);
    }

    @Getter
    @Setter
    public static class RegistrationActionData {
        public String delete;
        public String renew;
    }

    public Result action(PSF psf) {
        Form<RegistrationActionData> form = formFromRequest(RegistrationActionData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            RegistrationActionData data = form.get();
            if (data.renew != null) {
                dac().getRegistrationDao().renewRegistration(data.renew);
            } else if (data.delete == null) {
                // filter button
                return redirect(routes.RegistrationController.list(
                        psf.refilter(getStringMapFromForm(Registration.Field.class))
                ));
            } else if (data.delete.equals("[remove]")) {
                dac().getRegistrationDao().deleteExpiredRegistrations();
            } else {
                dac().getRegistrationDao().deleteRegistration(data.delete);
            }
            return redirect(routes.RegistrationController.listRegistrations());
        }
    }

}
