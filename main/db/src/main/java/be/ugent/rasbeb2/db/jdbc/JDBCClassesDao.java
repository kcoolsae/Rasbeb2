/*
 * JDBCClassesDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.ClassesDao;
import be.ugent.rasbeb2.db.dto.*;
import be.ugent.rasbeb2.db.poi.DataOrError;
import be.ugent.rasbeb2.db.util.PasswordGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class JDBCClassesDao extends JDBCAbstractDao implements ClassesDao {

    protected JDBCClassesDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public int createSchool(String name, String street, String zip, String town) {
        return insertInto("schools")
                .value("school_name", name)
                .value("school_street", street)
                .value("school_zip", zip)
                .value("school_town", town)
                .value("who_created", getUserId())
                .create();
    }


    public void editSchool(int schoolId, String name, String street, String zip, String town) {
        update("schools")
                .set("school_name", name)
                .set("school_street", street)
                .set("school_zip", zip)
                .set("school_town", town)
                .set("who_updated", getUserId())
                .where("school_id", schoolId)
                .execute();
    }

    @Override
    public void removeSchool(int schoolId) {
        deleteFrom("schools").where("school_id", schoolId).execute();
    }

    static School makeSchool(ResultSet rs) throws SQLException {
        return new School(
                rs.getInt("school_id"),
                rs.getString("school_name"),
                rs.getString("school_street"),
                rs.getString("school_zip"),
                rs.getString("school_town")
        );
    }

    private static User makeTeachers(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("user_name"),
                rs.getString("user_email"),
                Role.valueOf(rs.getString("user_role")),
                rs.getBoolean("user_disabled")
        );
    }

    private static ClassGroup makeClass(ResultSet rs) throws SQLException {
        return new ClassGroup(
                rs.getInt("class_id"),
                rs.getString("class_name")
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

    private SelectSQLStatement selectSchool() {
        return select("school_id, school_name, school_street, school_zip, school_town")
                .from("schools");
    }

    public School getSchool(int schoolId) {
        return selectSchool().where("school_id", schoolId).getOneObject(JDBCClassesDao::makeSchool);
    }

    public School getSchool() {
        return select("school_id, school_name, school_street, school_zip, school_town, user_id")
                .from("schools JOIN teachers USING(school_id)")
                .where("user_id", getUserId())
                .getOneObject(JDBCClassesDao::makeSchool);
    }

    public int getSchoolId() {
        // TODO move to different DAO
        return select("school_id")
                .from("teachers")
                .where("user_id", getUserId())
                .getOneInt();
    }

    public OptionalInt getSchoolId(int userId) {
        return select("school_id")
                .from("teachers")
                .where("user_id", userId)
                .findInt();
    }

    public SelectSQLStatement getSchoolStatement(int schoolId) {
        return select("user_id, user_name, user_email, user_role, user_disabled")
                .from("users JOIN teachers USING(user_id)")
                .where("school_id", schoolId);
    }

    public List<User> listAllTeachers(int schoolId) {
        return getSchoolStatement(schoolId).getList(JDBCClassesDao::makeTeachers);
    }

    private SelectSQLStatement selectTeachersWithSchool() {
        return select("user_id, user_name, user_email, user_role, user_disabled, school_name, school_id, school_town")
                .from("users JOIN teachers USING(user_id) JOIN schools USING(school_id)");
    }

    @Override
    public void disableTeacher(int userId) {
        update("users")
                .set("user_disabled = not user_disabled")
                .set("who_updated", getUserId())
                .where("user_id", userId)
                .execute();
    }

    public Iterable<ClassGroup> getClasses(int yearId) {
        return select("class_id, class_name")
                .from("classes")
                .where("year_id", yearId)
                .where("school_id", getSchool().id())
                .orderBy("class_id")
                .getList(JDBCClassesDao::makeClass);
    }

    public SelectSQLStatement selectClass(String name, int schoolId, int yearId) {
        return select("class_id").from("classes")
                .where("class_name", name)
                .where("school_id", schoolId)
                .where("year_id", yearId);
    }

    @Override
    public void addClasses(String classes, int yearId) {
        if (!classes.isBlank()) {
            int schoolId = getSchool().id();
            for (String c : classes.split("\\s*[,;]\\s*")) {
                if (selectClass(c.strip(), schoolId, yearId).isEmpty()) {
                    insertInto("classes")
                            .value("year_id", yearId)
                            .value("school_id", schoolId)
                            .value("class_name", c.strip())
                            .value("who_created", getUserId())
                            .execute();
                }
            }
        }
    }

    @Override
    public void editClass(String class_name, int classId) {
        update("classes").set("class_name", class_name).where("class_id", classId).execute();
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

    private List<Pupil> getPupils(int classId) {
        return select("pupil_id, pupil_name, pupil_gender, pupil_password")
                .from("pupils JOIN pupils_classes USING(pupil_id)")
                .where("class_id", classId)
                .getList(JDBCPupilDao::makePupil);
    }

    @Override
    public Iterable<ClassWithPupils> getClassesWithPupils(int yearId) {
        // TODO do this with one call to the database?
        Iterable<ClassGroup> classes = getClasses(yearId);
        ArrayList<ClassWithPupils> classesWithPupils = new ArrayList<>();
        for (ClassGroup c : classes) {
            classesWithPupils.add(new ClassWithPupils(c, getPupils(c.id())));
        }
        return classesWithPupils;
    }

    @Override
    public List<ClassGroup> listClasses(int schoolId, int yearId) {
        return select("class_id, class_name")
                .from("classes")
                .where("year_id", yearId)
                .where("school_id", schoolId)
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
        return ! select("1").from("pupils_classes JOIN pupils USING(pupil_id)")
                .where("class_id", classId)
                .where("pupil_name", pupilName)
                .isEmpty();
    }

    @Override
    public void addPupils(List<DataOrError<PupilInClass>> pupils) {
        for (DataOrError<PupilInClass> pupil : pupils) {
            int pupilId = insertInto("pupils")
                    .value("pupil_name", pupil.getData().name())
                    .value("pupil_gender", pupil.getData().gender())
                    .value("pupil_password", PasswordGenerator.generate())
                    .value("who_created", getUserId())
                    .create();

            insertInto("pupils_classes")
                    .value("pupil_id", pupilId)
                    .value("class_id", pupil.getData().classId())
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
                .where("school_id", getSchool().id())
                .orderBy("class_id")
                .getList(JDBCClassesDao::makePupilInClass);
    }

    public List<PupilInClass> getPupilsInClass(List<Integer> pupilIds) {
        return select("class_id, class_name, pupil_id, pupil_password, pupil_name, pupil_gender")
                .from("pupils JOIN pupils_classes USING(pupil_id) JOIN classes USING(class_id)")
                .where(String.format("pupil_id IN (%s)", pupilIds.stream().map(Object::toString).collect(Collectors.joining(", "))))
                .orderBy("class_id")
                .getList(JDBCClassesDao::makePupilInClass);
    }

    @Override
    public SchoolFinder findSchools() {
        return new JDBCSchoolFinder(selectSchool());
    }

    @Override
    public TeacherFinder findTeachers() {
        return new JDBCTeacherFinder(selectTeachersWithSchool());
    }

}
