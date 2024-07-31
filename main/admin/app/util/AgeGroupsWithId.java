/*
 * AgeGroupsWithId.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package util;

import be.ugent.rasbeb2.db.dto.AgeGroup;

import java.util.Iterator;
import java.util.List;

/**
 * Combines a list of (all) age groups with one age group singled out.
 * Used in age group tabs.
 */
public record AgeGroupsWithId(List<AgeGroup> list, int id) implements Iterable<AgeGroup> {

   public AgeGroupsWithId {
       if (id == 0) {
           id = list.getFirst().id();
       }
    }

    @Override
    public Iterator<AgeGroup> iterator() {
        return list.iterator();
    }

    public boolean isSelected(AgeGroup ageGroup) {
        return ageGroup.id() == id;
    }
}
