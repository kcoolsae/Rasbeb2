/*
 * JDBCClassesDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.OrderedSQLStatement;
import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.*;
import be.ugent.rasbeb2.db.util.PasswordGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class JDBCClassesDao extends JDBCAbstractDao implements ClassesDao {

    protected JDBCClassesDao(JDBCDataAccessContext context) {
        super(context);
    }

    private static ClassGroup makeClass(ResultSet rs) throws SQLException {
        return new ClassGroup(
                rs.getInt("class_id"),
                rs.getString("class_name")
        );
    }

    private static ClassWithPupils makeClassWithPupils(ResultSet rs) throws SQLException {
        return new ClassWithPupils(
                makeClass(rs),
                new ArrayList<>()
        );
    }

    static TeacherWithSchool makeTeachersWithSchool(ResultSet rs) throws SQLException {
        return new TeacherWithSchool(
                rs.getInt("user_id"),
                rs.getString("user_name"),
                rs.getString("user_email"),
                Role.valueOf(rs.getString("user_role")),
                rs.getBoolean("user_disabled"),
                rs.getString("school_name"),
                rs.getInt("school_id"),
                rs.getString("school_town")
        );
    }

    private OrderedSQLStatement selectClasses(int yearId) {
        return select("class_id, class_name")// id must be first!
                .from("classes")
                .where("year_id", yearId)
                .where("school_id", getSchoolId())
                .orderBy("class_name");
    }

    public Iterable<ClassGroup> getClasses(int yearId) {
        return selectClasses(yearId)
                .getList(JDBCClassesDao::makeClass);
    }

    @Override
    public void addClasses(String classes, int yearId) {
        if (!classes.isBlank()) {
            int schoolId = getSchoolId();
            for (String c : classes.split("\\s*[,;]\\s*")) {
                insertInto("classes")
                        .value("year_id", yearId)
                        .value("school_id", schoolId)
                        .value("class_name", c.strip())
                        .value("who_created", getUserId())
                        .execute();
            }
        }
    }

    @Override
    public void editClass(int classId, String className) {
        update("classes").set("class_name", className).where("class_id", classId).execute();
    }

    @Override
    public int addPupil(int classId, String name, Gender gender, String password) {
        int pupilId = insertInto("pupils")
                .value("pupil_name", name)
                .value("pupil_gender", gender)
                .value("pupil_password", password)
                .value("who_created", getUserId())
                .create();
        insertInto("pupils_classes").value("pupil_id", pupilId).value("class_id", classId).execute();
        return pupilId;
    }

    @Override
    public void editPupil(int pupilId, String name, Gender gender, String password) {
        update("pupils")
                .set("pupil_name", name)
                .set("pupil_gender", gender)
                .set("pupil_password", password)
                .set("who_updated", getUserId())
                .where("pupil_id", pupilId)
                .execute();
    }

    @Override
    public Iterable<ClassWithPupils> getClassesWithPupils(int yearId) {
        // uses the method in DAOHelper master/detail documentation
        Map<Integer, ClassWithPupils> map = selectClasses(yearId)
                .getMap(JDBCClassesDao::makeClassWithPupils);
        select("classes.class_id, pupil_id, pupil_name, pupil_gender, pupil_password") // class_id must be first!
                .from("""
                            pupils
                               JOIN pupils_classes USING(pupil_id)
                               JOIN classes ON pupils_classes.class_id = classes.class_id
                                                AND year_id = ? AND school_id = ?
                        """)
                .parameter(yearId)
                .parameter(getSchoolId())
                .processMap(map,
                        (c, rs) -> c.pupils().add(JDBCPupilContestDao.makePupil(rs))
                );
        return map.values();
    }

    @Override
    public List<ClassGroup> listClasses(int yearId) {
        return select("class_id, class_name")
                .from("classes")
                .where("year_id", yearId)
                .where("school_id", getSchoolId())
                .orderBy("class_name")
                .getList(JDBCClassesDao::makeClass);
    }

    @Override
    public void removeClass(int classId) {
        deleteFrom("classes").where("class_id", classId).execute();
    }

    @Override
    public void removePupil(int pupilId) {
        deleteFrom("pupils_classes").where("pupil_id", pupilId).execute();
        deleteFrom("pupils").where("pupil_id", pupilId).execute();
    }

    @Override
    public OptionalInt getClassId(String className, int yearId) {
        return select("class_id").from("classes")
                .where("year_id", yearId)
                .where("school_id", getSchoolId())
                .where("class_name", className)
                .findInt();
    }

    @Override
    public boolean pupilExistsInClass(String pupilName, int classId) {
        return !select("1").from("pupils_classes JOIN pupils USING(pupil_id)")
                .where("class_id", classId)
                .where("pupil_name", pupilName)
                .isEmpty();
    }

    @Override
    public void addPupils(List<PupilInClass> pupils) {
        for (PupilInClass pupil : pupils) {
            int pupilId = insertInto("pupils")
                    .value("pupil_name", pupil.name())
                    .value("pupil_gender", pupil.gender())
                    .value("pupil_password", PasswordGenerator.generate())
                    .value("who_created", getUserId())
                    .create();

            insertInto("pupils_classes")
                    .value("pupil_id", pupilId)
                    .value("class_id", pupil.classId())
                    .execute();
        }
    }

    private static PupilInClass makePupilInClass(ResultSet rs) throws SQLException {
        return new PupilInClass(
                rs.getInt("class_id"),
                rs.getString("class_name"),
                rs.getInt("pupil_id"),
                rs.getString("pupil_password"),
                rs.getString("pupil_name"),
                Gender.valueOf(rs.getString("pupil_gender"))
        );
    }

    @Override
    public List<PupilInClass> getPupilsByClass(int yearId) {
        return select("class_id, class_name, pupil_id, pupil_password, pupil_name, pupil_gender")
                .from("pupils JOIN pupils_classes USING(pupil_id) JOIN classes USING(class_id)")
                .where("year_id", yearId)
                .where("school_id", getSchoolId())
                .orderBy("class_name")
                .orderBy("pupil_name")
                .getList(JDBCClassesDao::makePupilInClass);
    }

    public List<PupilInClass> getPupilsInClass(List<Integer> pupilIds) {
        return select("class_id, class_name, pupil_id, pupil_password, pupil_name, pupil_gender")
                .from("pupils JOIN pupils_classes USING(pupil_id) JOIN classes USING(class_id)")
                .where("pupil_id = ANY (?)", pupilIds.toArray(new Integer[0])) // needs DAOHelper 1.1.13
                .orderBy("class_name")
                .orderBy("pupil_name")
                .getList(JDBCClassesDao::makePupilInClass);
    }

}
