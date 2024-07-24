/*
 * YearDaoTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.caagt.dao.ForeignKeyViolation;
import be.ugent.rasbeb2.db.dao.YearDao;
import be.ugent.rasbeb2.db.dto.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YearDaoTest extends OrganiserDaoTest {

    private YearDao dao;

    @BeforeEach
    void setUp() {
        dao = dac.getYearDao();
    }

    @Test
    void listAllYears() {
        List<Year> years = dao.listAllYears();

        assertThat(years).extracting(Year::name).containsExactly("2024-2025", "2023-2024", "2022-2023");
        assertThat(years).extracting(Year::id).containsExactly(24, 23, 22);
    }

    @Test
    void getYear() {
        Year year = dao.getYear(23);
        assertThat(year.name()).isEqualTo("2023-2024");
    }

    @Test
    void getRecentYear() {
        Year year = dao.getRecentYear();
        assertThat(year.name()).isEqualTo("2024-2025");
    }

    @Test
    void createYear() {
        dao.createYear("2025-2026");
        List<Year> years = dao.listAllYears();
        assertThat(years).extracting(Year::name).containsExactly("2025-2026", "2024-2025", "2023-2024", "2022-2023");
    }

    @Test
    void removeYear() {
        dao.removeYear(22);
        List<Year> years = dao.listAllYears();
        assertThat(years).extracting(Year::name).containsExactly("2024-2025", "2023-2024");
    }

    @Test
    void removeYearWithError() {
        assertThatThrownBy(() -> dao.removeYear(23))
                .isInstanceOf(ForeignKeyViolation.class);
    }

    @Test
    void updateYear() {
        dao.updateYear(23, "2023-2024 (updated)");
        Year year = dao.getYear(23);
        assertThat(year.name()).isEqualTo("2023-2024 (updated)");
    }
}
