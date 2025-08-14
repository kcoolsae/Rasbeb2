/*
 * RegExpTest.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2025 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db;


import be.ugent.rasbeb2.db.dao.ParticipationDao;
import be.ugent.rasbeb2.db.dto.QuestionWithFeedback;
import be.ugent.rasbeb2.db.dto.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegExpTest extends DaoTest {

    /** Marks for the given pupil, for question 4 */
    private int marksForPupil(int pupilId) {
        DataAccessContext context = DAP.getContext(pupilId,0, Role.PUPIL);
        context.begin();
        ParticipationDao dao = context.getParticipationDao();
        dao.closeParticipationAndComputeMarks(1);
        QuestionWithFeedback q = dao.getQuestionWithFeedback(1, 4, 1, "nl");
        context.commit();
        context.close();
        return q.marks();
    }

    @Test
    public void checkMarks() {
        assertThat (marksForPupil(1)).isEqualTo(6);
        assertThat (marksForPupil(2)).isEqualTo(6);
        assertThat (marksForPupil(3)).isEqualTo(-2);
        assertThat (marksForPupil(4)).isEqualTo(-2);
    }

}
