/*
 * EmailSendingController.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers.auth;

import common.DataAccessController;
import deputies.auth.EmailSendingDeputy;
import play.libs.mailer.MailerClient;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.function.Supplier;

public class EmailSendingController<D extends EmailSendingDeputy> extends DataAccessController<D> {

    @Inject
    private MailerClient mailerClient;

    public EmailSendingController(Supplier<D> deputyFactory) {
        super(deputyFactory);
    }

    @Override
    protected D createDeputy(Http.Request request) {
        D deputy = super.createDeputy(request);
        deputy.setMailerClient(mailerClient);
        return deputy;
    }
}
