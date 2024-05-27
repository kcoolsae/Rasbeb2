/*
 * Event.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import static be.ugent.rasbeb2.db.dto.EventExtendedStatus.*;

public record Event(int id, String title, String contestTitle, EventStatus eventStatus, ContestStatus contestStatus,
                    ContestType contestType, String lang) {

    public Event {
        if (title == null || title.isBlank()) {
            title = "Event #" + id;
        }
    }

    public EventExtendedStatus getExtendedStatus() {
        return switch (eventStatus) {
            case PENDING -> contestStatus == ContestStatus.OPEN ? PENDING_FREE : PENDING_BLOCKED;

            case OPEN -> switch (contestStatus) {
                case OPEN -> OPEN;
                case CLOSED -> CLOSED_FREE;
                default -> CLOSED_BLOCKED;
            };

            case CLOSED -> switch (contestStatus) {
                case OPEN -> contestType == ContestType.RESTRICTED ? CLOSED_FREE : CLOSED_BLOCKED;
                case CLOSED -> CLOSED_FREE;
                default -> CLOSED_BLOCKED;
            };
        };
    }

}
