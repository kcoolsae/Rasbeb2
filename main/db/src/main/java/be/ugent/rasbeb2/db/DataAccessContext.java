/*
 * DataAccessContext.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

/* Abstraction of a database connection. Provides data access objects. */

import be.ugent.rasbeb2.db.dao.*;

public interface DataAccessContext extends AutoCloseable {

    OrganiserDao getOrganiserDao ();

    RegistrationDao getRegistrationDao ();

    UserDao getUserDao();

    PupilContestDao getPupilContestDao();

    YearDao getYearDao();

    ClassesDao getClassesDao();

    SchoolDao getSchoolDao();

    ContestDao getContestDao();

    QuestionDao getQuestionDao();

    EventDao getEventDao();

    ParticipationDao getParticipationDao();

    ParticipationInfoDao getParticipationInfoDao();

    AgeGroupDao getAgeGroupDao();

    void begin();

    void commit();

    void rollback();

    @Override
    void close(); // overridden not to throw an exception
}
