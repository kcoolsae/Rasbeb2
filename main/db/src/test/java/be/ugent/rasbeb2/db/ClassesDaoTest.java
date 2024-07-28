/*
 * ClassDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.caagt.dao.ForeignKeyViolation;
import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassesDaoTest extends TeacherDaoTest {

    private ClassesDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getClassesDao();
    }

    @Test
    void getSchool() {
        // tests the single teacher related method from SchoolDao
        School school = dac.getSchoolDao().getSchool();
        assertThat(school.name()).isEqualTo("School, The");
    }

    @Test
    void listClasses() {
        List<ClassGroup> classGroups = dao.listClasses(24);
        assertThat(classGroups).extracting(ClassGroup::name)
                .containsExactly("3a", "3b");
    }

    @Test
    void getClassId() {
        assertThat(dao.getClassId("3a", 24)).hasValue(3);
    }

    @Test
    void removeClass() {
        dao.removeClass(2);
        List<ClassGroup> classGroups = dao.listClasses(23);
        assertThat(classGroups).extracting(ClassGroup::name)
                .containsExactly("Old 3a");
    }

    @Test
    void removeClassWithError() {
        assertThatThrownBy(() -> dao.removeClass(3))
                .isInstanceOf(ForeignKeyViolation.class);
    }

    @Test
    void addClasses() {
        dao.addClasses(" 4a el,  4b in ", 22);
        List<ClassGroup> classGroups = dao.listClasses(22);
        assertThat(classGroups).extracting(ClassGroup::name)
                .containsExactly("4a el", "4b in");
    }

    @Test
    void editClass() {
        dao.editClass(3, "3A");
        List<ClassGroup> classGroups = dao.listClasses(24);
        assertThat(classGroups).extracting(ClassGroup::name)
                .containsExactly("3A", "3b");
    }

    @Test
    void getClassesWithPupils() {
        Iterable<ClassWithPupils> classesWithPupils = dao.getClassesWithPupils(24);
        assertThat(classesWithPupils).extracting(ClassWithPupils::group)
                .extracting(ClassGroup::name)
                .containsExactly("3a", "3b");

        Iterator<ClassWithPupils> iterator = classesWithPupils.iterator();
        List<Pupil> first = iterator.next().pupils();
        List<Pupil> second = iterator.next().pupils();

        assertThat(first).extracting("name")
                .containsExactly("Pupil 1", "Pupil 2");
        assertThat(second).extracting("name")
                .containsExactly("Pupil 5", "Pupil 6");
    }

    @Test
    void pupilExistsInClass() {
        assertThat(dao.pupilExistsInClass("Pupil 1", 3)).isTrue();
        assertThat(dao.pupilExistsInClass("Pupil 0", 3)).isFalse();
    }

    @Test
    void addPupils() {
        List<PupilInClass> list = List.of(
                new PupilInClass(3, null, 0, null, "Pupil 7", Gender.MALE),
                new PupilInClass(3, null, 0, null, "Pupil 8", Gender.FEMALE),
                new PupilInClass(4, null, 0, null, "Pupil 9", Gender.FEMALE)
        );
        dao.addPupils(list);

        assertThat(dao.pupilExistsInClass("Pupil 7", 3)).isTrue();
        assertThat(dao.pupilExistsInClass("Pupil 8", 3)).isTrue();
        assertThat(dao.pupilExistsInClass("Pupil 9", 4)).isTrue();
    }

    @Test
    void getPupilsByClass() {
        List<PupilInClass> pupils = dao.getPupilsByClass(24);
        assertThat(pupils).flatExtracting(PupilInClass::className, PupilInClass::name)
                .containsExactly("3a", "Pupil 1", "3a", "Pupil 2", "3b", "Pupil 5", "3b", "Pupil 6");
        assertThat(pupils).flatExtracting(PupilInClass::classId, PupilInClass::pupilId)
                .containsExactly(3, 1, 3, 2, 4, 5, 4, 6);
        assertThat(pupils).extracting(PupilInClass::gender)
                .containsExactly(Gender.MALE, Gender.FEMALE, Gender.FEMALE, Gender.OTHER);
    }

    @Test
    void getPupilsInClass() {
        List<PupilInClass> pupils = dao.getPupilsInClass(List.of(6, 1));
        assertThat(pupils).flatExtracting(PupilInClass::className, PupilInClass::name)
                .containsExactly("3a", "Pupil 1", "3b", "Pupil 6");
    }

    @Test
    void addPupil() {
        int pupilId = dao.addPupil(4, "Pupil 1000", Gender.MALE, "password");
        Pupil pupil = dac.getUserDao().getPupil(pupilId, "password");
        assertThat(pupil).isEqualTo(
                new Pupil (pupilId, "Pupil 1000", Gender.MALE, "password")
        );
    }

    @Test
    void editPupil() {
        dao.editPupil(3, "Pupil 3 (updated)", Gender.MALE, "new password");
        Pupil pupil = dac.getUserDao().getPupil(3, "new password");
        assertThat(pupil).isEqualTo(
                new Pupil (3, "Pupil 3 (updated)", Gender.MALE, "new password")
        );
    }

    @Test
    void removePupil() {
        assertThat(dao.pupilExistsInClass("Pupil 4", 6)).isTrue();
        dao.removePupil(4);
        assertThat(dao.pupilExistsInClass("Pupil 4", 6)).isFalse();
    }

    @Test
    void removePupilError() {
        assertThatThrownBy(() -> dao.removePupil(3))
                .isInstanceOf(ForeignKeyViolation.class);
    }

}
