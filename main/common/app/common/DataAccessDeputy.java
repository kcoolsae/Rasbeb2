/*
 * DataAccessDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common;

import be.ugent.caagt.dao.Page;
import be.ugent.caagt.play.binders.PSF;
import be.ugent.caagt.play.controllers.Controller;
import be.ugent.rasbeb2.db.DataAccessContext;
import be.ugent.rasbeb2.db.dao.Finder;
import be.ugent.rasbeb2.db.dto.FieldEnum;
import play.mvc.Call;
import play.mvc.Result;

import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for deputies of {@link DataAccessController}. Provides a data access context and some helper methods for
 * use with paged tables.
 */
public class DataAccessDeputy extends Deputy {

    protected DataAccessContext dac() {
        return request.attrs().get(InjectDAC.DATA_ACCESS_CONTEXT);
    }

    @Override
    public void setParent (Controller<?> parent) throws RuntimeException {
        if (! (parent instanceof DataAccessController)) {
            throw new RuntimeException("Parent of " + getClass() + " should be instance of DataAccessController");
        }
    }

    public <D,F extends Enum<F> & FieldEnum<D>,S extends Finder<D,F,S>> Page<D> getPage (S finder, PSF psf, Class<F> enumClass) {
        for (F field : enumClass.getEnumConstants()) {
            Optional<String> filter = psf.getFilterValue(field.name());
            if (filter.isPresent()) {
                finder = finder.filter(field, filter.get());
            }
        }
        return finder.getPageOrderedBy(
                Enum.valueOf(enumClass, psf.getSortColumn()),
                psf.isAscending(),
                psf.getPageNr(),
                psf.getPageSize());
    }

    public Result resize (PSF psf, Function<PSF, Call> psfToCall) {
        int pageSize = getPageSizeFromForm();
        return redirect(psfToCall.apply(psf.resize(pageSize))).withCookies(createPageSizeCookie(pageSize));
    }

}
