/*
 * DifficultyLevels.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2026 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.util.List;

/**
 * Combines a question's bebras id with the list of difficulty levels (easy, medium, hard,...) for
 * all the age groups. Used by {@link be.ugent.rasbeb2.db.poi.DifficultyLevelSheetReader}.
 *
 */
public record DifficultyLevels(String externalId, List<String> levels) {
}
