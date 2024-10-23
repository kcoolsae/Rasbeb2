/*
 * SheetReader.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class SheetReader<T> {

    protected String getStringValue(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? null : new DataFormatter().formatCellValue(cell);
    }

    protected boolean isMissing(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Read one object of type T from a row.
     */
    public abstract void read(DataOrError<T> data, Row row);

    private boolean isEmptyRow(Row row) {
        if (row != null) {
            for (Cell cell : row) {
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    if (cell.getCellType() == CellType.STRING && !cell.getStringCellValue().isBlank()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Read a spreadsheet where one row equals one object of type T.
     */
    public List<DataOrError<T>> read(Path path) throws IOException {
        try (Workbook wb = WorkbookFactory.create(path.toFile())) {
            Sheet sheet = wb.getSheetAt(0);
            ArrayList<DataOrError<T>> result = new ArrayList<>();
            if (containsFormula(sheet)) {
                DataOrError<T> doe = new DataOrError<>(0);
                doe.addError("spreadsheet.errors.contains-formula");
                result.add(doe);
            } else {
                readRows(sheet, result);
            }
            return result;
        }
    }

    private void readRows(Sheet sheet, ArrayList<DataOrError<T>> result) {
        for (Row row : sheet) {
            if (!isEmptyRow(row)) {
                // ignore rows whose first cell starts with #
                Cell cell0 = row.getCell(0);
                if (cell0 == null || cell0.getCellType() != CellType.STRING || !cell0.getStringCellValue().startsWith("#")) {
                    DataOrError<T> doe = new DataOrError<>(row.getRowNum() + 1);
                    read(doe, row);
                    result.add(doe);
                }
            }
        }
    }

    private boolean containsFormula(Sheet sheet) {
        for (Row row : sheet) {
            for (Cell cell: row) {
                if (cell.getCellType() == CellType.FORMULA) {
                    return true;
                }
            }
        }
        return false;
    }

}
