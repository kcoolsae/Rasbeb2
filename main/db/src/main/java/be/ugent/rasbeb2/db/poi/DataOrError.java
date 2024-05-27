/*
 * DataOrError.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.poi;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data in a single row of a spreadsheet, including error information
 */
public class DataOrError<D> {

    private D data;
    public final int rowNumber;
    public List<String> errorCodes; // lazily created

    public DataOrError(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public void setData(D data) {
        this.data = data;
    }

    public void addError(String error) {
        if (errorCodes == null) {
            errorCodes = new ArrayList<>();
        }
        errorCodes.add(error);
    }

    public boolean hasError() {
        return errorCodes != null && !errorCodes.isEmpty();
    }

    public D getData() {
        return data;
    }
}
