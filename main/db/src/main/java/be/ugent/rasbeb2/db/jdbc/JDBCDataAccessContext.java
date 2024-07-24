/*
 * JDBCDataAccessContext.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.BaseDAC;
import be.ugent.rasbeb2.db.DataAccessContext;
import be.ugent.rasbeb2.db.dao.*;
import be.ugent.rasbeb2.db.dto.Role;

import java.sql.Connection;

public class JDBCDataAccessContext extends BaseDAC implements DataAccessContext {
    
    private final int userId;
    private final int schoolId;
    private final Role role;
            
    public JDBCDataAccessContext(Connection connection, int userId, int schoolId, Role role) {
        super(connection);
        this.userId = userId;
        this.schoolId = schoolId;
        this.role = role;
    }

    @Override
    public OrganiserDao getOrganiserDao() {
        return new JDBCOrganiserDao(this);
    }

    @Override
    public RegistrationDao getRegistrationDao() {
        return new JDBCRegistrationDao(this);
    }

    @Override
    public ClassesDao getClassesDao() {
        return new JDBCClassesDao(this);
    }

    @Override
    public SchoolDao getSchoolDao() {
        return new JDBCSchoolDao(this);
    }

    @Override
    public ContestDao getContestDao() {
        return new JDBCContestDao(this);
    }

    @Override
    public QuestionDao getQuestionDao() {
        return new JDBCQuestionDao(this);
    }

    @Override
    public EventDao getEventDao() {
        return new JDBCEventDao(this);
    }

    @Override
    public ParticipationDao getParticipationDao() {
        return new JDBCParticipationDao(this);
    }

    @Override
    public UserDao getUserDao() {
        return new JDBCUserDao(this);
    }

    @Override
    public PupilDao getPupilDao() {
        return new JDBCPupilDao(this);
    }

    @Override
    public YearDao getYearDao() {
        return new JDBCYearDao(this);
    }

    public int getUserId() {
        return userId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public Role getRole() {
        return role;
    }
}
