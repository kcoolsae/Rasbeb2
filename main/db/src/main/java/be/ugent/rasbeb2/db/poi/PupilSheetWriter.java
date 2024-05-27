/*
 * PupilSheetWriter.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import be.ugent.rasbeb2.db.dto.PupilInClass;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.function.Function;

public class PupilSheetWriter extends SheetWriter<PupilInClass> {

    public PupilSheetWriter(Function<String, String> i18nConverter) {
        super(i18nConverter);
    }

    @Override
    protected int getNrOfColumns() {
        return 5;
    }

    @Override
    protected List<String> getHeaderKeys() {
        return List.of (
                "spreadsheet.pupil.class", "spreadsheet.pupil.name", "spreadsheet.pupil.gender-short",
                "spreadsheet.pupil.bebras-id", "spreadsheet.pupil.password"
        );
    }

    @Override
    protected void fillRow(Row row, PupilInClass pupil) {
        row.createCell(0).setCellValue(pupil.className());
        row.createCell(1).setCellValue(pupil.name());
        row.createCell(2).setCellValue(i18nConverter.apply("enum.gender-short."+pupil.gender().name()));
        row.createCell(3).setCellValue(pupil.pupilId());
        row.createCell(4).setCellValue(pupil.password());
    }

}
