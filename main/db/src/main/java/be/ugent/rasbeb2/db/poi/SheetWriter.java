/*
 * SheetWriter.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public abstract class SheetWriter<E> {

    protected final Function<String, String> i18nConverter;

    protected SheetWriter(Function<String, String> i18nConverter) {
        this.i18nConverter = i18nConverter;
    }

    protected abstract int getNrOfColumns();

    protected List<String> getHeaderKeys() {
        return null;
    }

    protected byte[] write(Workbook workbook, Sheet sheet, List<E> elements) {
        int count = 0;
        List<String> headerKeys = getHeaderKeys();
        if (headerKeys != null) {
            // add header row starting with #
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("#" + i18nConverter.apply(headerKeys.getFirst()));
            for (int i = 1; i < headerKeys.size(); i++) {
                headerRow.createCell(i).setCellValue(i18nConverter.apply(headerKeys.get(i)));
            }
            count = 1;
        }
        for (E element : elements) {
            fillRow(sheet.createRow(count), element);
            count++;
        }
        for (int i = 0; i < getNrOfColumns(); i++) {
            sheet.autoSizeColumn(i);
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Could not create file to download", ex);
        }
    }

    protected abstract void fillRow(Row row, E element);

    public byte[] write(List<E> elements) {
        Workbook workbook = new XSSFWorkbook();
        return write(workbook, workbook.createSheet(), elements);
    }

    public byte[] write(List<E> elements, String title) {
        Workbook workbook = new XSSFWorkbook();
        return write(workbook, workbook.createSheet(WorkbookUtil.createSafeSheetName(title)), elements);
    }
}
