/*
 * ActiveYearOnly.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers;


import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Used with the @With-annotation to ensure that an action can only be executed if the selected year is an active year
 */
public class ActiveYearOnly extends Action.Simple {

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        if (Boolean.parseBoolean(request.session().get("year-active").orElse("false"))) {
            return delegate.call(request);
        } else {
            return CompletableFuture.completedFuture(unauthorized());
        }
    }
}
