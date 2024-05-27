/*
 * OrganiserOnly.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package controllers;

/**
 * Used with the @With-annotation to ensure that an action can only be executed when an organiser is logged in
 */
public class OrganiserOnly extends CheckRole {

    public OrganiserOnly() {
        super("ORGANISER");
    }

}
