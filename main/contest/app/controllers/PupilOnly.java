/*
 * PupilOnly.java
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
 * Used with the @With-annotation to ensure that an action can only be executed when a pupil is logged in
 */
public class PupilOnly extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        Http.Session session = request.session();
        if (session.get(Session.ID).isPresent() &&
                session.get(Session.NAME).isPresent()) {
            return delegate.call(request);
        } else {
            return CompletableFuture.completedFuture(unauthorized());
        }
    }
}
