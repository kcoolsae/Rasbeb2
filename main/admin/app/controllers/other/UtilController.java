/*
 * UtilController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.other;

import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.collection.immutable.Map;

import javax.inject.Inject;

public class UtilController extends Controller {

    @Inject
    private MessagesApi messagesApi;

    /**
     * Displays all keys from the messages object
     */
    public Result allKeys(Http.Request request) {
        Lang lang = messagesApi.preferred(request).lang();
        Map<String, String> map = messagesApi.asScala().messages().get(lang.code()).get();
        return ok(map.keys().mkString("\n"));
    }
}
