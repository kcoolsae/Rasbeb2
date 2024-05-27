/*
 * PupilSheetReader.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.Gender;
import be.ugent.rasbeb2.db.dto.PupilInClass;
import org.apache.poi.ss.usermodel.*;

import java.util.OptionalInt;
import java.util.function.Function;

/**
 * Reads list of pupils from a spreadsheet. The spreadsheet has the following columns, in that order:
 * <ul>
 *     <li>Classname</li>
 *     <li>Name</li>
 *     <li>Gender</li>
 * </ul>
 * Error codes (processed as warnings)
 * <ul>
 *     <li><code>spreadsheet.pupils.empty-class</code> No classname given</li>
 *     <li><code>spreadsheet.pupils.unknown-class</code> Invalid classname given</li>
 *     <li><code>spreadsheet.pupils.empty-name</code> No name given/li>
 *     <li><code>spreadsheet.pupils.empty-gender</code> No gender given</li>
 *     <li><code>spreadsheet.pupils.unknown-gender</code> Gender not one of the supported genders</li>
 *     <li><code>spreadsheet.pupils.duplicate-pupil</code> Pupil with the same name already exists in the class</li>
 * </ul>
 */
public class PupilSheetReader extends SheetReader<PupilInClass> {

    private final ClassesDao dao;
    private final int yearId;

    private final Function<String, Gender> genderTranslator;

    public PupilSheetReader(ClassesDao dao, int yearId, Function<String, Gender> genderTranslator) {
        this.dao = dao;
        this.yearId = yearId;
        this.genderTranslator = genderTranslator;
    }

    @Override
    public void read(DataOrError<PupilInClass> data, Row row) {
        String className = getStringValue(row, 0);
        String name = getStringValue(row, 1);
        String genderString = getStringValue(row, 2);

        OptionalInt classId = OptionalInt.empty();

        if (isMissing(className)) {
            data.addError("spreadsheet.pupils.empty-class");
        } else {
            classId = dao.getClassId(className, yearId);
            if (classId.isEmpty()) {
                data.addError("spreadsheet.pupils.unknown-class");
            }
        }

        if (isMissing(name)) {
            data.addError("spreadsheet.pupils.empty-name");
        }

        Gender gender = null;
        if (isMissing(genderString)) {
            data.addError("spreadsheet.pupils.empty-gender");
        } else {
            gender = genderTranslator.apply(genderString);
            if (gender == null) {
                data.addError("spreadsheet.pupils.unknown-gender");
            }
        }

        if (classId.isPresent()) {
            if (dao.pupilExistsInClass(name, classId.getAsInt())) {
                data.addError("spreadsheet.pupils.duplicate-pupil");
            }
        }

        data.setData(new PupilInClass(classId.orElse(0), className, 0, "", name, gender));
    }
}
