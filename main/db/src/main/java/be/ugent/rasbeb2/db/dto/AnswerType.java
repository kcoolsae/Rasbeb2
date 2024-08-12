/*
 * AnswerType.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public enum AnswerType {
    MC,
    INT,
    TEXT,
    JSON,
    CWC;  // not yet supported

    public boolean isInteractive() {
        return this == JSON || this == CWC;  // latter type not yet used
    }
}
