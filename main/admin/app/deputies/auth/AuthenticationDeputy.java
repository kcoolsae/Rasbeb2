/*
 * AuthenticationDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.auth;

import be.ugent.rasbeb2.db.dao.UserDao;
import be.ugent.rasbeb2.db.dto.User;
import be.ugent.rasbeb2.db.dto.Year;
import common.DataAccessDeputy;
import common.Session;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class AuthenticationDeputy extends DataAccessDeputy {

    @Getter
    @Setter
    public static class LoginData {
        @Constraints.Required
        public String login;
        @Constraints.Required
        public String password;
    }

    @Getter
    @Setter
    @Constraints.Validate
    public static class UpdatePasswordData implements Constraints.Validatable<List<ValidationError>> {
        @Constraints.Required
        @Formats.NonEmpty
        public String currentPassword;

        @Constraints.Required
        @Formats.NonEmpty
        public String newPassword;

        @Constraints.Required
        @Formats.NonEmpty
        public String confirmPassword;

        public List<ValidationError> validate() {
            if (currentPassword == null || newPassword == null || confirmPassword == null || newPassword.equals(confirmPassword)) {
                return null;
            } else {
                String messages = "auth.change-password.constraint";
                return Arrays.asList(
                        new ValidationError("newPassword", messages),
                        new ValidationError("confirmPassword", messages)
                );
            }
        }
    }

    public Result loginForm() {
        return ok(views.html.auth.login.render(emptyForm(LoginData.class), this));
    }

    private Map<String, String> getSessionVariables(User user) {
        Year recentYear = dac().getYearDao().getRecentYear();
        int schoolId = dac().getSchoolDao().getSchoolId(user.id()).orElse(0);
        return Map.of(
                Session.ID, Integer.toString(user.id()),
                Session.ROLE, user.role().name(),
                Session.NAME, user.name(),
                Session.YEAR, Integer.toString(recentYear.id()),
                Session.YEAR_NAME, recentYear.name(),
                Session.YEAR_ACTIVE, Boolean.toString(true),
                Session.SCHOOL_ID, Integer.toString(schoolId)
        );
    }

    public Result login() {
        Form<LoginData> form = formFromRequest(LoginData.class);
        if (form.hasErrors()) {
            return badRequest(views.html.auth.login.render(form, this));
        } else {
            LoginData data = form.get();
            User user = dac().getUserDao().getUser(data.login, data.password);
            if (user == null) {
                error("auth.login.error");
                return badRequest(views.html.auth.login.render(form, this));
            } else {
                return redirect(controllers.home.routes.HomeController.index()).withSession(
                        getSessionVariables(user)
                );
            }
        }
    }

    public Result logout() {
        String parent = findInSession(Session.PARENT);
        if (parent == null) {
            success("auth.logout.message");
            return redirect(controllers.home.routes.HomeController.index()).withNewSession();
        } else {
            success("auth.logout.mimic");
            return redirect(controllers.home.routes.HomeController.index()).withSession(
                    getSessionVariables(dac().getUserDao().getUser(Integer.parseInt(parent)))
            );
        }
    }


    public Result changePasswordForm() {
        return ok(views.html.auth.change_password.render(
                emptyForm(UpdatePasswordData.class), this
        ));
    }

    public Result changePassword() {
        Form<UpdatePasswordData> form = formFromRequest(UpdatePasswordData.class);
        if (!form.hasErrors()) {
            UpdatePasswordData data = form.get();
            UserDao dao = dac().getUserDao();
            if (dao.hasPassword(data.currentPassword) && data.newPassword.equals(data.confirmPassword)) {
                dao.updatePassword(getCurrentUserId(), data.newPassword);
                success("auth.change-password.success");
                return redirect(controllers.home.routes.HomeController.index());
            } else {
                form = form.withError("currentPassword", "auth.change-password.error");
            }
        }
        // errors fall through
        return badRequest(views.html.auth.change_password.render(form, this));
    }


}
