/*
 * AsOrganiserTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;

import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dto.Participation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Common super class of database tests run as a pupil (5 of class 4 of school 1)
 */
class AnonymousDaoTest extends DaoTest {

    protected DataAccessContext dac;

    @BeforeEach
    void setupDac() {
        dac = DAP.getContext(7, 0, null);
        dac.begin();
    }

    @AfterEach
    void tearDownDac() {
        dac.rollback();
        dac.close();
    }

    @Test
    void createParticipation() {
        // pupil 7 is anonymous
        ParticipationDao dao = dac.getParticipationDao();
        dao.createParticipation(
                5, 1, "en", 7
        );

        Participation actual = dao.getParticipation(5);
        Participation expected = new Participation(5, 7, 1, "en", 0, false,
                actual.timeLeftInSeconds(), 50*60, actual.deadline()
        );
        assertThat(actual).isEqualTo(expected);
        // timing cannot easily be tested
        assertThat(actual.timeLeftInSeconds()).isCloseTo(50*60, within(2));
        assertThat(actual.deadline()).isCloseTo(
                Instant.now().plus(50, java.time.temporal.ChronoUnit.MINUTES),
                within(2, java.time.temporal.ChronoUnit.SECONDS)
        );
    }
}
