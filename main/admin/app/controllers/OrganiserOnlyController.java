/*
 * OrganiserOnlyController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers;

import common.DataAccessController;
import deputies.OrganiserOnlyDeputy;
import play.mvc.With;

import java.util.function.Supplier;

/**
 * Common super class of all controllers that require an organiser to be logged in.
 */
@With(OrganiserOnly.class)
public class OrganiserOnlyController<D extends OrganiserOnlyDeputy> extends DataAccessController<D> {

    public OrganiserOnlyController(Supplier<D> deputyFactory) {
        super(deputyFactory);
    }
}
