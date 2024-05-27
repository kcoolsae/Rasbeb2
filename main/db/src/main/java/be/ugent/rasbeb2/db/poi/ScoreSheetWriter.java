/*
 * ScoreSheetWriter.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import be.ugent.rasbeb2.db.dto.PupilWithScore;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.function.Function;

public class ScoreSheetWriter extends SheetWriter<PupilWithScore> {

    public ScoreSheetWriter(Function<String, String> i18nConverter) {
        super(i18nConverter);
    }

    @Override
    protected int getNrOfColumns() {
        return 3;
    }

    @Override
    protected List<String> getHeaderKeys() {
        return List.of (
                "spreadsheet.score.class", "spreadsheet.score.name", "spreadsheet.score.score"
        );
    }

    @Override
    protected void fillRow(Row row, PupilWithScore pupil) {
        row.createCell(0).setCellValue(pupil.className());
        row.createCell(1).setCellValue(pupil.name());
        if (pupil.maxMarks() > 0) {
            row.createCell(2).setCellValue(pupil.marks());
        }
    }

}
