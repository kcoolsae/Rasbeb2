/*
 * LanguageDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.other;

import lombok.Getter;
import lombok.Setter;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import views.html.chooseLanguage;

import java.time.Duration;

public class LanguageDeputy extends common.Deputy {
    // note: uses no database access


    public Result chooseLanguage() {
        return ok(chooseLanguage.render(
                getUILanguagesInfo(),
                this
        ));
    }

    @Getter @Setter
    public static class LanguageData {
        public String language;
    }

    public Result setLanguage() {
        Form<LanguageData> form = formFromRequest(LanguageData.class);
        if (form.hasErrors()) {
            return badRequest(); // should not happen
        } else {
            success("change-language.message");
            return indexWithLanguage(form.get().language);
        }
    }

    /**
     * Starting page specifying language
     */
    public Result indexWithLanguage(String language) {
        return redirect(controllers.auth.routes.HomeController.index()).withCookies(
                Http.Cookie.builder(getMessagesApi().langCookieName(), language)
                        .withMaxAge(Duration.ofDays(365))
                        .build()
            );
    }

}
