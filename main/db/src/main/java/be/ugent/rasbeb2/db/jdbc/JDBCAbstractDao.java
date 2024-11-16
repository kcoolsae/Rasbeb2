/*
 * JDBCAbstractDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.BaseDAO;

public class JDBCAbstractDao extends BaseDAO {

    protected JDBCAbstractDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    protected JDBCDataAccessContext getContext() {
        return (JDBCDataAccessContext) super.getContext();
    }

    public int getUserId() {
        return getContext().getUserId();
    }

    public int getSchoolId() {
        return getContext().getSchoolId();
    }

}
