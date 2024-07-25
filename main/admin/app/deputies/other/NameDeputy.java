/*
 * NameDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.other;

import be.ugent.rasbeb2.db.dao.UserDao;
import common.DataAccessDeputy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.data.Form;
import play.mvc.Result;

public class NameDeputy extends DataAccessDeputy {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        public String name;
    }

    public Result changeNameForm() {
        return ok(views.html.other.change_name.render(
                formFromData(new UserData(getFromSession("name"))), this
        ));
    }

    public Result changeName() {
        Form<UserData> form = formFromRequest(UserData.class);
        if (form.hasErrors()) {
            return badRequest();
        } else {
            UserDao dao = dac().getUserDao();
            String newName = form.get().name;
            dao.updateUsername(newName);
            success("change-name.message");
            return redirect(controllers.home.routes.HomeController.index())
                    .addingToSession(request, "name", newName);
        }
    }


}
