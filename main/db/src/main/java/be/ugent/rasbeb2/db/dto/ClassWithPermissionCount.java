/*
 * ClassWithPermissionCount.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2026 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.dto;

import java.security.Permission;
import java.util.Collection;

public record ClassWithPermissionCount (ClassGroup group, PermissionCount permissionCount){

    public enum PermissionCount {
        NONE, SOME, ALL, EMPTY;

        static PermissionCount of (Collection<PupilWithPermission> pupils) {
            if (pupils.isEmpty()) {
                return EMPTY;
            } else {
                int count  = 0;
                for (PupilWithPermission pupil : pupils) {
                    if (pupil.permitted()) {
                        count++;
                    }
                }
                if (count == pupils.size()) {
                    return ALL;
                } else if (count == 0) {
                    return NONE;
                } else {
                    return SOME;
                }
            }
        }
    }

    public ClassWithPermissionCount(ClassWithPermissions cwp) {
        this (cwp.group(), PermissionCount.of(cwp.pupils()));
    }
}
