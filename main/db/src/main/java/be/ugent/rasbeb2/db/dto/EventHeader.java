/*
 * EventHeader.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

public record EventHeader (int id, String title, String contestTitle, String lang, String ageGroupName) {
    public EventHeader {
        if (title == null || title.isBlank()) {
            title = "Event #" + id;
        }
        if (contestTitle == null || contestTitle.isBlank()) {
            contestTitle = "Contest #" + id;
        }
        if (ageGroupName == null || ageGroupName.isBlank()) {
            ageGroupName = "Age group #" + id;
        }
    }
}
