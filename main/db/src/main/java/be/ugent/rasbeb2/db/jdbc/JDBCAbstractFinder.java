/*
 * JDBCAbstractFinder.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.Page;
import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.Finder;
import be.ugent.rasbeb2.db.dto.FieldEnum;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class JDBCAbstractFinder<D, F extends Enum<F> & FieldEnum<D>, SELF extends Finder<D, F, SELF>> implements Finder<D, F, SELF> {

    protected final SelectSQLStatement stat;

    public JDBCAbstractFinder(SelectSQLStatement stat) {
        this.stat = stat;
    }

    protected abstract SELF create(SelectSQLStatement stat);

    protected abstract D makeRecord(ResultSet rs) throws SQLException;

    private SELF whereContains(String fieldName, String value) {
        // my need to become protected
        return create(stat.where(fieldName + " ILIKE '%'||?||'%'", value));
    }

    protected SELF whereContains(F field, String value) {
        return whereContains(field.getFieldName(), value);
    }

    private SELF whereStartsWith(String fieldName, String value) {
        // my need to become protected
        return create(stat.where(fieldName + " ILIKE ?||'%'", value));
    }

    protected SELF whereStartsWith(F field, String value) {
        return whereStartsWith(field.getFieldName(), value);
    }

    private Page<D> getPageOrderedBy(String fieldName, boolean asc, int pageNr, int pageSize) {
        // my need to become protected
        return stat.orderBy(fieldName, asc).onlyPage(pageNr, pageSize).getPage(this::makeRecord);
    }

    public Page<D> getPageOrderedBy(F field, boolean asc, int pageNr, int pageSize) {
        return getPageOrderedBy(field.getFieldName(), asc, pageNr, pageSize);
    }
}
