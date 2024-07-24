/*
 * JDBCSchoolDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.caagt.dao.helper.SelectSQLStatement;
import be.ugent.rasbeb2.db.dao.SchoolDao;
import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.dto.School;
import be.ugent.rasbeb2.db.dto.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.OptionalInt;

public class JDBCSchoolDao extends JDBCAbstractDao implements SchoolDao {

    protected JDBCSchoolDao(JDBCDataAccessContext context) {
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

    @Override
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

    private SelectSQLStatement selectSchool() {
        return select("school_id, school_name, school_street, school_zip, school_town")
                .from("schools");
    }

    @Override
    public School getSchool(int schoolId) {
        return selectSchool()
                .where("school_id", schoolId)
                .getOneObject(JDBCSchoolDao::makeSchool);
    }

    @Override
    public School getSchool() {
        return selectSchool()
                .where("school_id", getSchoolId())
                .getOneObject(JDBCSchoolDao::makeSchool);
    }

    @Override
    public OptionalInt getSchoolId(int userId) {
        return select("school_id")
                .from("teachers")
                .where("user_id", userId)
                .findInt();
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

    public List<User> listAllTeachers(int schoolId) {
        return select("user_id, user_name, user_email, user_role, user_disabled")
                .from("users JOIN teachers USING(user_id)")
                .where("school_id", schoolId)
                .getList(JDBCSchoolDao::makeTeachers);
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


    @Override
    public SchoolFinder findSchools() {
        return new JDBCSchoolFinder(selectSchool());
    }

    @Override
    public TeacherFinder findTeachers() {
        return new JDBCTeacherFinder(selectTeachersWithSchool());
    }

}
