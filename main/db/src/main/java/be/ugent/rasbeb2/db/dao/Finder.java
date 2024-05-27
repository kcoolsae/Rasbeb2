/*
 * Finder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.caagt.dao.Page;
import be.ugent.rasbeb2.db.dto.FieldEnum;

public interface Finder<D,F extends FieldEnum<D>, SELF extends Finder<D, F, SELF>> {

    /**
     * Filter on the given value for the given fieldName
     */
    SELF filter (F field, String value);

     /**
     * Return a single page of schools, ordered by the given field
     */
    Page<D> getPageOrderedBy (F field, boolean asc, int pageNr, int pageSize);
}
