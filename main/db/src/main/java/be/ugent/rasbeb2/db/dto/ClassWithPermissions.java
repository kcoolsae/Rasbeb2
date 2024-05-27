/*
 * ClassWithPermissions.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.util.ArrayList;
import java.util.List;

public record ClassWithPermissions(ClassGroup group, List<PupilWithPermission> pupils){
    // record with empty list
    public ClassWithPermissions(ClassGroup group) {
        this(group, new ArrayList<>());
    }
}
