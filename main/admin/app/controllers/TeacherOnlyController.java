/*
 * TeacherOnlyController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers;

import common.DataAccessController;
import deputies.TeacherOnlyDeputy;
import play.mvc.With;

import java.util.function.Supplier;


/**
 * Common super class of all controllers that require a teacher to be logged in.
 */
@With(TeacherOnly.class)
public class TeacherOnlyController <D extends TeacherOnlyDeputy> extends DataAccessController<D> {

    public TeacherOnlyController(Supplier<D> deputyFactory) {
        super(deputyFactory);
    }
}
