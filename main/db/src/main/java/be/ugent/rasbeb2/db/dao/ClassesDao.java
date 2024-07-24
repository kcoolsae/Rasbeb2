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

/**
 * DAO for handling classes and pupils
 */
public interface ClassesDao {

    /* =======
       CLASSES
       ======= */

    void addClasses(String classes, int yearId);

    int addPupil(int classId, String name, Gender gender, String password);

    void editPupil(int pupilId, String name, Gender gender, String password);

    /**
     * Lists the classes of the school of the current user for the given year
     */
    List<ClassGroup> listClasses(int yearId);

    void editClass(String class_name, int classId);

    void removeClass(int classId);

    /**
     * Returns the id of the class with the given name in the given year and in the school of the current user
     */
    OptionalInt getClassId(String className, int yearId);

    /* =======
       PUPILS IN CLASSES
       ======= */

    Iterable<ClassWithPupils> getClassesWithPupils(int yearId);

    boolean pupilExistsInClass(String pupilName, int classId);

    void addPupils(List<DataOrError<PupilInClass>> pupils);

    List<PupilInClass> getPupilsByClass(int yearId);

    List<PupilInClass> getPupilsInClass(List<Integer> pupilIds);

    void removePupil(int pupilId);
}
