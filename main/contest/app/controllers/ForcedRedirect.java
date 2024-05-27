/*
 * ForcedRedirect.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers;

import common.Session;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Used with the @With-annotation to ensure that an action redirects to either the question page or
 * the feedback page if the session contains the corresponding session variable
 */
public class ForcedRedirect extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        Http.Session session = request.session();
        if (session.get(Session.ID).isPresent()) {
            if (session.get(Session.CONTEST).isPresent()) {
                return CompletableFuture.supplyAsync(() -> redirect(controllers.contest.routes.ParticipationController.question(0)));
            } else if (session.get(Session.FEEDBACK).isPresent()) {
                return CompletableFuture.supplyAsync(() -> redirect(controllers.contest.routes.FeedbackController.show()));
            }
        }
        return delegate.call(request);
    }
}
