/*
 * JDBCOrganiserDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.rasbeb2.db.dao.OrganiserDao;
import be.ugent.rasbeb2.db.dto.Role;

public class JDBCOrganiserDao extends JDBCAbstractDao implements OrganiserDao {

    protected JDBCOrganiserDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public boolean noOrganisersRegistered() {
        return select("1").from("users").where("user_role", Role.ORGANISER).isEmpty();
    }
}
