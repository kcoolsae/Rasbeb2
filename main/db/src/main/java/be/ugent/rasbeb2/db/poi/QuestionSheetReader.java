/*
 * QuestionSheetReader.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import be.ugent.rasbeb2.db.dto.AnswerType;
import be.ugent.rasbeb2.db.dto.QuestionWithTranslations;
import be.ugent.rasbeb2.db.dto.Translation;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * Reads list of questions from a spreadsheet. The spreadsheet has the following columns:
 * <ul>
 *     <li>External ID</li>
 *     <li>Answer type</li>
 *     <li>Extra answer type information</li>
 * </ul>
 * and then for each language
 * <ul>
 *     <li>Title</li>
 *     <li>The correct solution</li>
 * </ul>
 * Error codes (processed as warnings)
 * <ul>
 *     <li><code>question.blank-external-id</code> No external ID given - question not generated</li>
 *     <li><code>question.invalid-answer-type</code> Invalid answer type - changed into multiple choice</li>
 * </ul>
 */
public class QuestionSheetReader extends SheetReader<QuestionWithTranslations> {

    private final List<String> langs;
    private final int size;

    public QuestionSheetReader(List<String> langs) {
        this.langs = langs;
        this.size = langs.size();
    }

    @Override
    public void read(DataOrError<QuestionWithTranslations> data, Row row) {
        AnswerType answerType;
        try {
            answerType = AnswerType.valueOf(getStringValue(row, 1).strip());
        } catch (IllegalArgumentException | NullPointerException ignored) {
            answerType = AnswerType.MC;
            data.addError("question.upload.invalid-answer-type");
        }

        String externalId = getStringValue(row, 0);
        if (isMissing(externalId)) {
            data.addError("question.upload.blank-external-id");
        }

        Translation[] translations = new Translation[size];

        data.setData(
                new QuestionWithTranslations(
                        externalId.strip(), answerType, getStringValue(row, 2), translations
                )
        );

        // read translation information
        for (int i = 0; i < size; i++) {
            translations[i] = new Translation(
                    langs.get(i), getStringValue(row, 2 * i + 3).strip(), getStringValue(row, 2 * i + 4).strip()
            );
        }
    }
}
