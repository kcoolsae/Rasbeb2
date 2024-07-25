/*
 * AuthenticationDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.auth;

import be.ugent.rasbeb2.db.dto.Pupil;
import common.Session;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Result;

import java.util.Map;


public class AuthenticationDeputy extends deputies.ContestDeputy {

    @Getter
    @Setter
    public static class LoginData {
        @Constraints.Required
        public int id;
        @Constraints.Required
        public String password;
    }

    public Result loginForm() {
        if (pupilLoggedIn()) {
            return redirect(controllers.auth.routes.HomeController.index());
        } else {
            return ok(views.html.auth.login.render(emptyForm(LoginData.class), this));
        }
    }

    public Result login() {
        Form<LoginData> form = formFromRequest(LoginData.class);
        if (form.hasErrors()) {
            return badRequest(views.html.auth.login.render(form, this));
        } else {
            LoginData data = form.get();
            Pupil pupil = dac().getPupilDao().getPupil(data.id, data.password);
            if (pupil == null) {
                error("auth.login.error");
                LOGGER.info("{} {} login failed", data.id, data.password);
                return badRequest(views.html.auth.login.render(form, this));
            } else {
                LOGGER.info("{} login", pupil.id());
                return redirect(controllers.auth.routes.HomeController.index()).withSession(
                        Map.of(
                                Session.ID, Integer.toString(pupil.id()),
                                Session.NAME, pupil.name()
                        )
                );
            }
        }
    }

    public Result logout() {
        LOGGER.info ("{} logout", getCurrentUserId());
        success("auth.logout.message");
        return redirect(controllers.auth.routes.HomeController.index()).withNewSession();
    }

}
