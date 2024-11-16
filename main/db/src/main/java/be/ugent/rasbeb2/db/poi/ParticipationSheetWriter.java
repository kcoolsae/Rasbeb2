/*
 * ParticipationSheetWriter.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import be.ugent.rasbeb2.db.dto.ParticipationInfo;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.Function;

public class ParticipationSheetWriter extends SheetWriter<ParticipationInfo> {

    public ParticipationSheetWriter(Function<String, String> i18nConverter) {
        super(i18nConverter);
    }

    // simple version, no column headers

    @Override
    protected int getNrOfColumns() {
        return 8;
    }

    @Override
    protected void fillRow(Row row, ParticipationInfo info) {
        row.createCell(0).setCellValue(info.pupilId());
        row.createCell(1).setCellValue(info.pupilName());
        row.createCell(2).setCellValue(info.lang());
        row.createCell(3).setCellValue(i18nConverter.apply(
                "enum.gender-short."+info.pupilGender().name())
        );
        row.createCell(4).setCellValue(info.schoolNameWithTown());
        row.createCell(5).setCellValue(info.className());
        row.createCell(6).setCellValue(info.age_group_id());
        row.createCell(7).setCellValue(info.marks());
    }

}
