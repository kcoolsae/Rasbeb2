/*
 * JDBCRegistrationDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.RegistrationDao;
import be.ugent.rasbeb2.db.dto.Registration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class JDBCRegistrationDao extends JDBCAbstractDao implements RegistrationDao {

    protected JDBCRegistrationDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public String addPasswordRequest(String email) {
        String token = UUID.randomUUID().toString();
        insertOrUpdateInto("password_requests")
                .key("user_email", email.toLowerCase().strip())
                .value("request_token", token)
                .value("request_expires", Instant.now().plus(1, ChronoUnit.HOURS))
                .execute();
        return token;
    }

    @Override
    public boolean isValidPasswordRequest(String email, String token) {
        return ! select("1").from("password_requests")
                .where("user_email", email.toLowerCase().strip())
                .where("request_token", token)
                .where("request_expires > now()")
                .isEmpty();
    }

    @Override
    public void deletePasswordRequest(String email) {
        deleteFrom("password_requests")
                .where("user_email", email.toLowerCase().strip())
                .execute();
    }

    @Override
    public String addRegistration(String email, int schoolId) {
        String token = UUID.randomUUID().toString();
        insertOrUpdateInto("registrations")
                .key("user_email", email.toLowerCase().strip())
                .value("school_id", schoolId)
                .value("registration_token", token)
                .value("registration_expires", Instant.now().plus(72, ChronoUnit.HOURS))
                .execute();
        return token;
    }

    @Override
    public boolean isValidRegistration(String email, String token, int schoolId) {
        return ! select("1").from("registrations")
                .where("user_email", email.toLowerCase().strip())
                .where("school_id", schoolId)
                .where("registration_token", token)
                .where("registration_expires > now()")
                .isEmpty();
    }

    @Override
    public void deleteRegistration(String email) {
        deleteFrom("registrations")
                .where("user_email", email.toLowerCase().strip())
                .execute();
    }

    static Registration makeRegistration(ResultSet rs) throws SQLException {
        return new Registration(
                rs.getString("user_email"),
                rs.getString("school_name"),
                rs.getTimestamp("registration_expires")
        );
    }

    public SelectSQLStatement selectRegistrations() {
        return select("user_email, school_name, registration_expires")
                .from("registrations JOIN schools USING(school_id)");
    }

    @Override
    public RegistrationDao.RegistrationFinder findRegistrations() {
        return new JDBCRegistrationFinder(selectRegistrations());
    }

    @Override
    public void deleteExpiredRegistrations() {
        deleteFrom("registrations")
                .where("registration_expires < now()")
                .execute();
    }
}
