/*
 * PasswordGenerator.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.util;

import java.util.Random;

public class PasswordGenerator {

    private static final String[] VOWELS = {
            "a", "e", "o", "ou", "i"
    };
    private static final String[] CONSONANTS = {
            "b", "bl", "br", "c", "d", "dr", "f", "fl", "fr", "g", "gr",
            "l", "m", "n", "p", "pl", "pr", "r", "s", "st", "t", "tr", "v", "vr", "z"
    };
    private static final String[] TERMINALS = {
            "l", "s", "t", "x", "n", "f", "st", "ns", "nt"
    };

    private static final Random RG = new Random();

    /**
     * Generates a random 'pronounceable' password
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder(10);

        if (RG.nextInt(3) == 0) {
            sb.append(VOWELS[RG.nextInt(VOWELS.length)]);
        }
        for (int i = 0; i < 3; i++) {
            sb.append(CONSONANTS[RG.nextInt(CONSONANTS.length)]);
            sb.append(VOWELS[RG.nextInt(VOWELS.length)]);
        }

        if (RG.nextInt(3) != 0) {
            sb.append(TERMINALS[RG.nextInt(TERMINALS.length)]);
        }
        return sb.toString();
    }

}
