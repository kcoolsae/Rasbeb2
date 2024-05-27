/*
 * InjectDAC.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package common;

import be.ugent.rasbeb2.db.DataAccessContext;
import be.ugent.rasbeb2.db.DataAccessProvider;
import be.ugent.rasbeb2.db.dto.Role;
import play.libs.typedmap.TypedKey;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Used with the @With-annotation to ensure that makes all action methods in such a controller run in a database transaction
 * and provides a data access context for them.
 */
public class InjectDAC extends Action.Simple {

    public static final TypedKey<DataAccessContext> DATA_ACCESS_CONTEXT = TypedKey.create("DataAccessContext");

    @Inject
    DataAccessProvider dap;

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        Optional<String> id = request.session().get(Session.ID);
        Optional<String> schoolId = request.session().get(Session.SCHOOL_ID);
        Optional<Role> role = request.session().get(Session.ROLE).map(Role::valueOf);
        try (DataAccessContext dac = dap.getContext(
                Integer.parseInt(id.orElse("0")),
                Integer.parseInt(schoolId.orElse("0")),
                role.orElse(null))) {
            try {
                dac.begin();
                CompletionStage<Result> result = delegate.call(request.addAttr(DATA_ACCESS_CONTEXT, dac));
                dac.commit();
                return result;
            } catch (Exception ex) {
                dac.rollback();
                throw ex;
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Action terminated by checked exception", ex);
        }
    }

}

