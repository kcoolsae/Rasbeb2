/*
 * ClassesDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.*;
import be.ugent.rasbeb2.db.poi.DataOrError;

import java.util.List;
import java.util.OptionalInt;

public interface ClassesDao {

    int createSchool (String name, String street, String zip, String town);

    void editSchool(int schoolId, String name, String street, String zip, String town);

    void removeSchool(int schoolId);

    interface SchoolFinder extends Finder<School, School.Field, SchoolFinder> {}
    SchoolFinder findSchools ();

    School getSchool(int schoolId);

    /**
     * Return the school of the current user.
     */
    School getSchool();

    /**
     * Return the id of the school of the current user
     */
    int getSchoolId();

    /**
     * Return the id of the school of the given user.
     */
    OptionalInt getSchoolId(int userId);

    List<User> listAllTeachers(int schoolId);

    interface TeacherFinder extends Finder<TeacherWithSchool, TeacherWithSchool.Field, TeacherFinder> {}
    TeacherFinder findTeachers ();

    void disableTeacher(int userId);

    void addClasses(String classes, int yearId);

    int addPupil(int classId, String name, Gender gender, String password);

    void editPupil(int pupilId, String name, Gender gender, String password);

    Iterable<ClassWithPupils> getClassesWithPupils(int yearId);

    List<ClassGroup> listClasses(int schoolId, int yearId);

    void editClass(String class_name, int classId);

    void removeClass(int classId);

    void removePupil(int pupilId);

    /**
     * Returns the id of the class with the given name in the given year and in the school of the current user
     */
    OptionalInt getClassId(String className, int yearId);

    boolean pupilExistsInClass(String pupilName, int classId);

    void addPupils(List<DataOrError<PupilInClass>> pupils, int yearId);

    List<PupilInClass> getPupilsByClass(int yearId);

    List<PupilInClass> getPupilsInClass(List<Integer> pupilIds);
}
