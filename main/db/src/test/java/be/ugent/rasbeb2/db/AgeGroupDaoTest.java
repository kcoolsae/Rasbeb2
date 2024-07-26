/*
 * AgeGroupDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.AgeGroupDao;
import be.ugent.rasbeb2.db.dto.AgeGroup;
import be.ugent.rasbeb2.db.dto.AgeGroupWithDuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AgeGroupDaoTest extends TeacherDaoTest {

    private AgeGroupDao dao;

    @BeforeEach
    void setupDao() {
        dao = dac.getAgeGroupDao();
    }

    @Test
    void getAllAgeGroups() {
        List<AgeGroup> ageGroups = dao.getAllAgeGroups("fr");
        assertThat(ageGroups).extracting(AgeGroup::description)
                .containsExactly("Description of age group 1 in fr", "Description of age group 2 in fr", "Description of age group 3 in fr");
        assertThat(ageGroups).extracting(AgeGroup::name)
                .containsExactly("Age group 1 in fr", "Age group 2 in fr", "Age group 3 in fr");
    }

    @Test
    void getAgeGroups() {
        assertThat(dao.getAgeGroups(5, "nl")).extracting(AgeGroup::description)
                .containsExactly("Description of age group 1 in nl", "Description of age group 3 in nl");
    }

    @Test
    void getAgeGroupsWithDuration() {
        assertThat(dao.getAgeGroupsWithDuration(5, "nl"))
                .flatExtracting(agwd -> agwd.ageGroup().id(), AgeGroupWithDuration::duration)
                .containsExactly(1, 50, 2, null, 3, 30);
    }

    @Test
    void updateDuration() {
        dao.updateDuration(5, 1, 60);
        assertThat(dao.getAgeGroupsWithDuration(5, "nl"))
                .flatExtracting(agwd -> agwd.ageGroup().id(), AgeGroupWithDuration::duration)
                .containsExactly(1, 60, 2, null, 3, 30);
    }

    @Test
    void addDuration() {
        dao.updateDuration(5, 2, 55);
        assertThat(dao.getAgeGroupsWithDuration(5, "nl"))
                .flatExtracting(agwd -> agwd.ageGroup().id(), AgeGroupWithDuration::duration)
                .containsExactly(1, 50, 2, 55, 3, 30);
    }

    @Test
    void removeAgeGroup() {
         dao.removeAgeGroup(5, 3);
         assertThat(dao.getAgeGroups(5, "nl"))
                 .extracting(AgeGroup::name)
                 .containsExactly("Age group 1 in nl");
    }
}
