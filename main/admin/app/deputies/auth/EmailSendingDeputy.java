/*
 * EmailSendingDeputy.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package deputies.auth;

import common.DataAccessDeputy;
import lombok.Getter;
import lombok.Setter;
import play.data.validation.Constraints;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

public class EmailSendingDeputy extends DataAccessDeputy {


    // RFC 5322 compliant regex with an optional role (#role) at the end, for example: john.doe@email.com#admin
    protected static final String MAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])(#[a-zA-z0-9-]+)?";
    private MailerClient mailerClient;

    public void setMailerClient(MailerClient mailerClient) {
        this.mailerClient = mailerClient;
    }

    protected void sendEmail(String subject, String to, String text) {
        // removes fragment #... at end of 'to' address
        int pos = to.lastIndexOf('#');
        if (pos >= 0) {
            to = to.substring(0, pos);
        }
        Email email = new Email()
                .setSubject(subject)
                .setFrom(config.getString("rasbeb2.no-reply-address"))
                .addTo(to)
                .setBodyText(text);
        mailerClient.send(email);
    }

    protected String hostUri() {
        // TODO is there a more reliable way to do this?
        String host = request.host();
        if (host.endsWith("443")) {
            return "https://" + host;
        } else {
            return "http://" + host;
        }
    }

    @Getter
    @Setter
    public static class EmailData {
        @Constraints.Required
        @Constraints.Pattern(value= MAIL_PATTERN, message="error.email")
        public String email;
    }
}
