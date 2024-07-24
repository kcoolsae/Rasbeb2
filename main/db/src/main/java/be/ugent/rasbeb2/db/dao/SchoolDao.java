/*
 * SchoolDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.School;
import be.ugent.rasbeb2.db.dto.TeacherWithSchool;
import be.ugent.rasbeb2.db.dto.User;

import java.util.List;
import java.util.OptionalInt;

/**
 * DAO for handling schools and teachers
 */
public interface SchoolDao {

    /* =======
       SCHOOLS
       ======= */

    int createSchool(String name, String street, String zip, String town);

    void editSchool(int schoolId, String name, String street, String zip, String town);

    void removeSchool(int schoolId);

    interface SchoolFinder extends Finder<School, School.Field, SchoolDao.SchoolFinder> {
    }

    SchoolDao.SchoolFinder findSchools();

    /**
     * The school with given id
     */
    School getSchool(int schoolId);

    /* =======
       SCHOOLS OF TEACHERS
       ======= */

    /**
     * The id of the school of the given user (teacher).
     */
    OptionalInt getSchoolId(int userId);

    /**
     * The school of the current user.
     */
    School getSchool();

    /* =======
       TEACHERS
       ======= */

    List<User> listAllTeachers(int schoolId);

    interface TeacherFinder extends Finder<TeacherWithSchool, TeacherWithSchool.Field, SchoolDao.TeacherFinder> {
    }

    SchoolDao.TeacherFinder findTeachers();

    void disableTeacher(int userId);


}
