/*
 * JDBCUserDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.rasbeb2.db.dao.UserDao;
import be.ugent.rasbeb2.db.dto.Pupil;
import be.ugent.rasbeb2.db.dto.Role;
import be.ugent.rasbeb2.db.dto.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCUserDao extends JDBCAbstractDao implements UserDao {

    protected JDBCUserDao(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public int createUser(String name, String email, Role role) {
        return insertInto("users")
                .value("user_name", name)
                .value("user_email", email.toLowerCase().strip())
                .value("user_role", role)
                .value("who_created", getUserId())
                .create();
    }

    @Override
    public void createTeacher(int userId, int schoolId) {
        insertInto("teachers").value("user_id", userId).value("school_id", schoolId).create();
    }

    @Override
    public int createAnonymousPupil() {
        return insertInto("pupils")
                .value("pupil_name", "")
                .value("pupil_password", "")
                .value("pupil_anonymous", true)
                .create();
    }

    @Override
    public Pupil getPupil(int pupilId, String password) {
        return select("pupil_id, pupil_name, pupil_gender, pupil_password")
                .from("pupils")
                .where("pupil_id", pupilId)
                .where("pupil_password", password)
                .getObject(JDBCPupilContestDao::makePupil);
    }

    @Override
    public void updatePassword(int userId, String password) {
        String[] sh = SecurityUtils.saltAndHash(password);
        update("users")
                .set("user_password_salt", sh[0])
                .set("user_password_hash", sh[1])
                .set("who_updated", getUserId())
                .where("user_id", userId)
                .execute();
    }

    @Override
    public void updateUsername(String username) {
        int userId = getUserId();
        update("users")
                .set("user_name", username)
                .set("who_updated", userId)
                .where("user_id", userId)
                .execute();
    }

    private record UserWithPasswordInfo(User user, String salt, String hash) {

    }

    private static UserWithPasswordInfo makeUserWithPasswordInfo(ResultSet rs) throws SQLException {
        return new UserWithPasswordInfo(
                makeUser(rs),
                rs.getString("user_password_salt"),
                rs.getString("user_password_hash")
        );
    }

    private static User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("user_name"),
                rs.getString("user_email"),
                Role.valueOf(rs.getString("user_role")),
                rs.getBoolean("user_disabled")
        );
    }

    @Override
    public User getUser(String email, String password) {
        UserWithPasswordInfo u =
                select("user_id, user_name, user_email, user_role, user_password_salt, user_password_hash, user_disabled")
                        .from("users")
                        .where("user_email", email.toLowerCase().strip())
                        .getObject(JDBCUserDao::makeUserWithPasswordInfo);

        if (u == null || !SecurityUtils.isCorrectPassword(u.salt(), u.hash(), password)) {
            return null;
        } else {
            return u.user();
        }
    }

    private record PasswordInfo(String salt, String hash, boolean disabled) {

    }

    private static PasswordInfo makePasswordInfo(ResultSet rs) throws SQLException {
        return new PasswordInfo(
                rs.getString("user_password_salt"),
                rs.getString("user_password_hash"),
                rs.getBoolean("user_disabled")
        );
    }

    public boolean hasPassword(String password) {
        int userId = getUserId();
        if (userId == 0) {
            return false;
        } else {
            PasswordInfo info = select("user_password_salt, user_password_hash, user_disabled")
                    .from("users")
                    .where("user_id", userId)
                    .getObject(JDBCUserDao::makePasswordInfo);
            return !info.disabled && SecurityUtils.isCorrectPassword(info.salt(), info.hash(), password);
        }
    }

    @Override
    public int getUserId(String email) {
        return select("user_id")
                .from("users")
                .where("user_email", email.toLowerCase().strip())
                .getOneInt();
    }

    @Override
    public User getUser(int userId) {
        return select("user_id, user_name, user_email, user_role, user_password_salt, user_password_hash, user_disabled")
                .from("users")
                .where("user_id", userId)
                .getOneObject(JDBCUserDao::makeUser);
    }

    @Override
    public boolean isKnownEmailAddress(String email) {
        return !select("1").from("users")
                .where("user_email", email.toLowerCase().strip())
                .isEmpty();
    }
}
