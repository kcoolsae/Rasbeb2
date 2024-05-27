/*
 * DataAccessController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common;

import be.ugent.caagt.play.controllers.Controller;
import play.mvc.With;

import java.util.function.Supplier;

/**
 * Common super class of all controllers that need an injected database
 */
@With(InjectDAC.class)
public class DataAccessController<D extends DataAccessDeputy> extends Controller<D> {

    public DataAccessController(Supplier<D> deputyFactory) {
        super(deputyFactory);
    }
}
