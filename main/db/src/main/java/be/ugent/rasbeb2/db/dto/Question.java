/*
 * Question.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record Question (QuestionHeader header, String typeExtra, String magicQ, String magicF) {

    public int id() {
        return header.id();
    }

    public boolean isInteractive() {
        return header.answerType().isInteractive();
    }
}
