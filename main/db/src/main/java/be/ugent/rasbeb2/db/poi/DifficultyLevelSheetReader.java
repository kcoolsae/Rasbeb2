/*
 * DifficultyLevelSheetReader.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2026 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import be.ugent.rasbeb2.db.dto.DifficultyLevels;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads difficulty levels for various questions and age groups
 * from a spreadsheet. The spreadsheet has the following columns:
 * <ul>
 *     <li>External ID of the question</li>.
 * </ul>
 * and then for each age group
 * <ul>
 *     <li>Name of the difficulty level (easy,medium,hard,...)</li>
 * </ul>
 * Difficulty levels can be blank and trailing difficulty levels can be committed,
 * in other words, not all lists of difficulty levels are the same length.</p>
 * <p>Error codes (processed as warnings):
 * <ul>
 *     <li><code>question.blank-external-id</code> No external ID given - question not generated</li>
 * </ul></p>
 */
public class DifficultyLevelSheetReader extends SheetReader<DifficultyLevels> {

    @Override
    public void read(DataOrError<DifficultyLevels> data, Row row) {
        String externalId = getStringValue(row, 0);
        if (isMissing(externalId)) {
            data.addError("question.upload.blank-external-id");
            return; // question not generated
        }

        List<String> levels = new ArrayList<>();
        for (int i = 1; i < row.getLastCellNum(); i++) {
            String value = getStringValue(row, i);
            levels.add(value == null ? "" : value.strip());
        }

        data.setData(new DifficultyLevels(externalId.strip(), levels));
    }
}
