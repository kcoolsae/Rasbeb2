/*
 * EventExtendedStatus.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

/**
 * Extended status of an event, depending on the event status, the status of the contest to which it refers, and the type of contest
 */
public enum EventExtendedStatus {

    /**
     * Event is newly created and can be opened, because its contest is open.
     */
    PENDING_FREE,

    /**
     * Event is newly created but cannot be opened, because its contest is not yet or no longer open.
     */
    PENDING_BLOCKED,

    /**
     * Participation is possible, because both the event and its contest are open
     */
    OPEN,

    /**
     * Participation is no longer possible, because the event or its (official) contest is closed. Results are available.
     */
    CLOSED_FREE,

    /**
     * Participations is no longer possible, because the event is closed. Results are not yet available until
     * the corresponding (official) contest is closed.
     */
    CLOSED_BLOCKED

}
