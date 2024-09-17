/*
 * PasswordDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.auth;

import be.ugent.caagt.dao.DataAccessException;
import be.ugent.rasbeb2.db.dao.RegistrationDao;
import be.ugent.rasbeb2.db.dao.UserDao;
import controllers.auth.routes;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Result;
import views.html.auth.reset_password;

import java.util.Arrays;
import java.util.List;

/**
 * Handles password change and reset
 */
public class PasswordDeputy extends EmailSendingDeputy {

    public Result forgotPasswordForm() {
        return ok(views.html.auth.forgot_password.render(emptyForm(EmailData.class), this));
    }

    public Result forgotPassword() {
        Form<EmailData> form = formFromRequest(EmailData.class);
        if (form.hasErrors()) {
            return badRequest(views.html.auth.forgot_password.render(form, this));
        } else {
            String email = form.get().email;
            if (dac().getUserDao().isKnownEmailAddress(email)) {
                String token = dac().getRegistrationDao().addPasswordRequest(email);
                sendEmail(
                        i18n("auth.forgot-password.mail.subject"),
                        email,
                        i18n("auth.forgot-password.mail.text",
                                hostUri() + routes.PasswordController.resetPasswordForm(token)
                        )
                );

            }
            success("auth.forgot-password.message");
            return redirect(routes.AuthenticationController.loginForm());
        }
    }

    public Result resetPasswordForm(String token) {
        return ok(reset_password.render(
               emptyForm(NewPasswordData.class), token, this
        ));
    }

    public Result resetPassword(String token) {

        Form<NewPasswordData> form = formFromRequest(NewPasswordData.class);
        if (form.hasErrors()) {
            return badRequest(reset_password.render(form, token, this));
        } else {
            NewPasswordData data = form.get();
            RegistrationDao registrationDao = dac().getRegistrationDao();
            if (registrationDao.isValidPasswordRequest(data.email, token)) {
                UserDao userDao = dac().getUserDao();
                try {
                    int userId = userDao.getUserId(data.email);
                    // update password
                    userDao.updatePassword(userId, data.password);
                    // delete token
                    registrationDao.deletePasswordRequest(data.email);
                    success("auth.reset-password.success");
                    return redirect(controllers.home.routes.HomeController.index());
                } catch (DataAccessException ex) {
                    error("auth.reset-password.error");
                    return badRequest(reset_password.render(form, token, this));
                }
            } else {
                error("auth.reset-password.error");
                return badRequest(reset_password.render(form, token, this));
            }
        }
    }

    @Getter
    @Setter
    @Constraints.Validate
    public static class NewPasswordData extends EmailData implements Constraints.Validatable<List<ValidationError>> {

        @Constraints.Required
        @Formats.NonEmpty
        public String password;

        @Constraints.Required
        @Formats.NonEmpty
        public String repeated;

        public List<ValidationError> validate() {
            if (password == null || repeated == null || password.equals(repeated)) {
                return null;
            } else {
                String messages = "auth.change-password.constraint";
                return Arrays.asList(
                        new ValidationError("password", messages),
                        new ValidationError("repeated", messages)
                );
            }
        }

    }
}
