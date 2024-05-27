/*
 * CheckRole.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers;

import common.Session;
import play.api.mvc.Call;
import play.i18n.MessagesApi;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Checks whether the session is still active, and redirects to a login page if it is not
 */
abstract class CheckRole extends Action.Simple{

    @Inject
    private MessagesApi messagesApi;

    private final String roleString;

    protected CheckRole(String roleString) {
        this.roleString = roleString;
    }

    protected Call loginCall() {
        return controllers.auth.routes.AuthenticationController.loginForm();
    }

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        Http.Session session = request.session();
        if (session == null || session.get(Session.ID).isEmpty()) {
            return CompletableFuture.completedFuture(
                    redirect(loginCall()).flashing(
                            "warning", messagesApi.preferred(request).apply("auth.login.session-warning")
                    )
            );
        } else if (session.get(Session.ROLE).orElse("").equals(roleString)) {
            return delegate.call(request);
        } else {
            return CompletableFuture.completedFuture(unauthorized());
        }
    }
}
