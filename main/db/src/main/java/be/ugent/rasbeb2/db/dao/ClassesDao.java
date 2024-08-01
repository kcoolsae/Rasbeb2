/*
 * ClassesDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dao;

import be.ugent.rasbeb2.db.dto.ClassGroup;
import be.ugent.rasbeb2.db.dto.ClassWithPupils;
import be.ugent.rasbeb2.db.dto.Gender;
import be.ugent.rasbeb2.db.dto.PupilInClass;

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

    /**
     * Lists the classes of the school of the current user for the given year
     */
    List<ClassGroup> listClasses(int yearId);

    void editClass(int classId, String className);

    void removeClass(int classId);

    /**
     * Returns the id of the class with the given name in the given year and in the school of the current user
     */
    OptionalInt getClassId(String className, int yearId);

    /* =======
       PUPILS IN CLASSES
       ======= */

    // classes are ordered by name

    Iterable<ClassWithPupils> getClassesWithPupils(int yearId);

    boolean pupilExistsInClass(String pupilName, int classId);

    /**
     * Creates new pupils and adds them to the listed classes.
     * Only uses name, gender and class id of the {@link PupilInClass} objectass. Generates a new password for each pupil.
     */
    void addPupils(List<PupilInClass> pupils);

    List<PupilInClass> getPupilsByClass(int yearId);

    List<PupilInClass> getPupilsInClass(List<Integer> pupilIds);

    int addPupil(int classId, String name, Gender gender, String password);

    void editPupil(int pupilId, String name, Gender gender, String password);

    void removePupil(int pupilId);
}
