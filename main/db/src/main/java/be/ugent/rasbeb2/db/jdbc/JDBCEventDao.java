/*
 * JDBCEventDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.EventDao;
import be.ugent.rasbeb2.db.dto.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public class JDBCEventDao extends JDBCAbstractDao implements EventDao {
    protected JDBCEventDao(JDBCDataAccessContext context) {
        super(context);
    }

    public int getSchoolId() {
        return select("school_id, user_id")
                .from("schools JOIN teachers USING(school_id)")
                .where("user_id", getUserId())
                .getOneObject(rs -> rs.getInt("school_id"));
    }

    @Override
    public void addEvent(int contestId, int ageGroupId, int yearId, String title, String lang) {
        insertInto("events")
                .value("contest_id", contestId)
                .value("age_group_id", ageGroupId)
                .value("school_id", getSchoolId())
                .value("year_id", yearId)
                .value("lang", lang)
                .value("event_title", title)
                .value("who_created", getUserId())
                .execute();
    }

    static Event makeEvent(ResultSet rs) throws SQLException {
        return new Event(
                makeEventHeader(rs),
                rs.getInt("contest_id"),
                EventStatus.valueOf(rs.getString("event_status")),
                ContestStatus.valueOf(rs.getString("contest_status")),
                ContestType.valueOf(rs.getString("contest_type"))
        );
    }

    private static EventHeader makeEventHeader(ResultSet rs) throws SQLException {
        return new EventHeader(
                rs.getInt("event_id"),
                rs.getString("event_title"),
                rs.getString("contest_title"),
                rs.getString("lang"),
                rs.getString("age_group_name")
        );
    }

    public SelectSQLStatement selectEvents() {
        return select("""
                    event_id, event_title, contest_title, lang, age_group_name,
                    event_status, contest_id, contest_status, contest_type""")
                .from("""
                        events JOIN contests USING(contest_id)
                          JOIN age_groups USING(age_group_id,lang)
                          LEFT JOIN contests_i18n USING(contest_id, lang)""");
    }

    @Override
    public List<Event> listEvents(int yearId) {
        return selectEvents()
                .where("school_id", getSchoolId())
                .where("year_id", yearId)
                .getList(JDBCEventDao::makeEvent);
    }

    @Override
    public Event getEvent(int eventId) {
        return selectEvents()
                .where("event_id", eventId)
                .getObject(JDBCEventDao::makeEvent);
    }

    @Override
    public EventHeader getEventHeader(int eventId) {
        return select("event_id, event_title, contest_title, lang, age_group_name")
                .from("""
                events JOIN contests_i18n USING(contest_id, lang)
                       JOIN age_groups USING(age_group_id, lang)""")
                .where("event_id", eventId)
                .getObject(JDBCEventDao::makeEventHeader);
    }

    public String getEventTitle(int eventId) {
        return select("event_title")
                .from("events")
                .where("event_id", eventId)
                .getOneString();
    }

    @Override
    public boolean isCorrectSchool(int eventId) {
        return !select("1")
                .from("events JOIN teachers USING(school_id)")
                .where("user_id", getUserId())
                .where("event_id", eventId)
                .isEmpty();
    }

    @Override
    public List<Integer> getSelectedPupils(int eventId) {
        return select("pupil_id")
                .from("permissions")
                .where("event_id", eventId)
                .getList(rs -> rs.getInt("pupil_id"));
    }

    private static ParticipationWithPupil makeParticipation(ResultSet rs) throws SQLException {
        Timestamp deadline = rs.getTimestamp("participation_deadline");
        return new ParticipationWithPupil(
                rs.getInt("pupil_id"),
                rs.getInt("contest_id"),
                rs.getString("pupil_name"),
                rs.getBoolean("participation_closed"),
                deadline == null ? null : deadline.toInstant(),
                rs.getInt("participation_total_marks"),
                rs.getInt("max_marks")
        );
    }

    @Override
    public List<ParticipationWithPupil> getParticipations(int eventId) {
        return select("pupil_id, contest_id, pupil_name, participation_closed, participation_deadline, participation_total_marks, (max_marks - min_marks) as max_marks")
                .from("""
                        permissions JOIN pupils using(pupil_id)
                                    LEFT JOIN participations using(pupil_id, event_id)
                                    LEFT JOIN contests_ag USING(contest_id, age_group_id)
                                    LEFT JOIN question_sets USING(contest_id, age_group_id)""")
                .where("event_id", eventId)
                .orderBy("pupil_name")
//                .orderBy("participation_closed")  // we should ask the teacher how he wants to see the list
//                .orderBy("participation_deadline")
                .getList(JDBCEventDao::makeParticipation);
    }

    @Override
    public void participationAddExtraMinutes(int contestId, int pupilId, int minutesToAdd) {
        update("participations")
                .set(String.format("participation_deadline = participation_deadline + interval '%d minute'", minutesToAdd)) // cannot easily be done with ? syntax
                .where("contest_id", contestId)
                .where("pupil_id", pupilId)
                .execute();
    }

    @Override
    public void reopenParticipation(int contestId, int pupilId) {
        update("participations").set("participation_closed", false)
                .where("contest_id", contestId)
                .where("pupil_id", pupilId)
                .execute();
    }

    @Override
    public void selectPupil(int eventId, int pupilId) {
        insertInto("permissions")
                .value("pupil_id", pupilId)
                .value("event_id", eventId)
                .value("who_created", getUserId())
                .execute();
    }

    @Override
    public void updateClassPermissions(int eventId, int classId, Iterable<Integer> selected) {
        // first delete all permissions for this class and event
        deleteFrom("permissions USING pupils_classes")
                .where("permissions.pupil_id = pupils_classes.pupil_id")
                .where("permissions.event_id", eventId)
                .where("pupils_classes.class_id", classId)
                .execute();

        // now insert the selected pupils
        for (Integer pupilId : selected) {
            insertInto("permissions")
                    .value("pupil_id", pupilId)
                    .value("event_id", eventId)
                    .value("who_created", getUserId())
                    .execute();
        }

    }

    @Override
    public Iterable<ClassWithPermissions> listClassesWithPermissions(int eventId) {
        // all classes for school linked to event
        Map<Integer, ClassWithPermissions> map = select("class_id, class_name")
                .from("classes JOIN events USING (school_id)")
                .where("event_id", eventId)
                .where("classes.year_id = events.year_id")
                .orderBy("class_name")
                .getMap(rs -> rs.getInt("class_id"),
                        JDBCEventDao::makeClassWithPermissions
                );
        // all permissions for students in classes linked to event
        select("class_id, pupil_id, pupil_name, permissions.event_id IS NOT NULL AS has_permission")
                .from("""
                        events
                        JOIN classes USING (school_id)
                        JOIN pupils_classes USING (class_id)
                        JOIN pupils USING (pupil_id)
                        LEFT JOIN permissions USING (pupil_id, event_id)""")
                .where("event_id", eventId)
                .where("classes.year_id = events.year_id")
                .processMap(map,
                        (c, rs) -> c.pupils().add(makePupilWithPermission(rs))
                );
        return map.values();
    }

    private static ClassWithPermissions makeClassWithPermissions(ResultSet rs) throws SQLException {
        return new ClassWithPermissions(
                new ClassGroup(rs.getInt("class_id"), rs.getString("class_name"))
        );
    }

    private static PupilWithPermission makePupilWithPermission(ResultSet rs) throws SQLException {
        return new PupilWithPermission(
                rs.getInt("pupil_id"),
                rs.getString("pupil_name"),
                rs.getBoolean("has_permission")
        );
    }

    @Override
    public void editEvent(int eventId, String title) {
        update("events")
                .set("event_title", title)
                .where("event_id", eventId)
                .execute();
    }

    @Override
    public void openEvent(int eventId) {
        update("events")
                .set("event_status", EventStatus.OPEN)
                .where("event_id", eventId)
                .execute();
    }

    @Override
    public void closeEvent(int eventId) {
        ContestType contestType = select ("contest_type")
                .from("events JOIN contests USING(contest_id)")
                .where("event_id", eventId)
                .getEnum(ContestType.class);
        switch (contestType) {
            case OFFICIAL -> update("events")
                    .set("event_status", EventStatus.CLOSED)
                    .where("event_id", eventId)
                    .execute();
            case RESTRICTED -> call("close_event(?,?)")
                    .parameter(eventId)
                    .parameter(getUserId())
                    .execute();
        }

    }

    private static PupilWithScore makePupilWithScore(ResultSet rs) throws SQLException {
        return new PupilWithScore(
                rs.getInt("pupil_id"),
                rs.getString("pupil_name"),
                rs.getString("class_name"),
                rs.getInt("participation_total_marks"),
                rs.getInt("max_marks")
        );
    }

    @Override
    public List<PupilWithScore> getPupilsWithScore(int eventId) {
        return select("pupil_id, pupil_name, class_name, participation_total_marks, (max_marks - min_marks) as max_marks")
                .from("""
                        permissions JOIN pupils using(pupil_id)
                            JOIN pupils_classes using(pupil_id)
                            JOIN classes using(class_id)
                            LEFT JOIN participations using(pupil_id, event_id)
                            LEFT JOIN contests_ag USING(contest_id, age_group_id)
                            LEFT JOIN question_sets USING(contest_id, age_group_id)""")
                .where("event_id", eventId)
                .orderBy("class_name")
                .orderBy("pupil_name")
                .getList(JDBCEventDao::makePupilWithScore);
    }


    @Override
    public boolean isOpen(int eventId) {
        return ! select("1")
                .from ("events JOIN contests USING (contest_id)")
                .where ("event_id", eventId)
                .where ("event_status = 'OPEN'::event_status")
                .where ("contest_status = 'OPEN'::contest_status")
                .isEmpty();
    }
}
