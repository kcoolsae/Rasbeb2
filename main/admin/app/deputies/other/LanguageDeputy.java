/*
 * LanguageDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.other;

import common.Deputy;
import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import views.html.chooseLanguage;

import java.time.Duration;

public class LanguageDeputy extends Deputy {
    // note: uses no database access

    public Result chooseLanguage() {
        return ok(chooseLanguage.render(
                getUILanguagesInfo(),
                this
        ));
    }

    @Getter @Setter
    public static class LanguageData {
        public String redirectTo;
        public String language;
    }

    public Result setLanguage() {
        Form<LanguageData> form = formFromRequest(LanguageData.class);
        if (form.hasErrors()) {
            return badRequest(); // should not happen
        } else {
            success("change-language.message");
            Result r;
            if (form.get().redirectTo.isEmpty()) {
                r = redirect(controllers.home.routes.HomeController.index());
            } else {
                r = redirect(form.get().redirectTo);
            }
            return r.withCookies(
                    Http.Cookie.builder(getMessagesApi().langCookieName(), form.get().language)
                            .withMaxAge(Duration.ofDays(365))
                            .build()
            );
        }
    }

}
