/*
 * JDBCAgeGroupDao.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright © 2023-2024 Kris Coolsaet (Universiteit Gent)
 *
 * This software is distributed under the MIT License - see files LICENSE and AUTHORS
 * in the top level project directory.
 */

package be.ugent.rasbeb2.db.jdbc;

import be.ugent.rasbeb2.db.dao.AgeGroupDao;
import be.ugent.rasbeb2.db.dto.AgeGroup;
import be.ugent.rasbeb2.db.dto.AgeGroupWithDuration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

class JDBCAgeGroupDao extends JDBCAbstractDao implements AgeGroupDao {

    JDBCAgeGroupDao (JDBCDataAccessContext dac) {
        super(dac);
    }

    private static final String AGE_GROUP_FIELDS = "age_groups.age_group_id, age_group_name, age_group_description";

    private static AgeGroup makeAgeGroup(ResultSet rs) throws SQLException {
        return new AgeGroup(
                rs.getInt("age_group_id"),
                rs.getString("age_group_name"),
                rs.getString("age_group_description")
        );
    }

    static AgeGroupWithDuration makeAgeGroupWithDuration(ResultSet rs) throws SQLException {
        return new AgeGroupWithDuration(
                makeAgeGroup(rs),
                rs.getObject("contest_duration", Integer.class)
        );
    }

    @Override
    public List<AgeGroup> getAgeGroups(int contestId, String lang) {
        return select(AGE_GROUP_FIELDS)
                .from("age_groups JOIN contests_ag USING(age_group_id)")
                .where("lang", lang)
                .where("contest_id", contestId)
                .orderBy("age_groups.age_group_id")
                .getList(JDBCAgeGroupDao::makeAgeGroup);
    }

    @Override
    public List<AgeGroupWithDuration> getAgeGroupsWithDuration(int contestId, String lang) {
        return select(AGE_GROUP_FIELDS + ", contest_duration")
                .from("age_groups LEFT JOIN contests_ag ON(age_groups.age_group_id=contests_ag.age_group_id AND contest_id = ?)")
                .parameter(contestId)
                .where("lang", lang)
                .orderBy("age_groups.age_group_id")
                .getList(JDBCAgeGroupDao::makeAgeGroupWithDuration);
    }

    @Override
    public void removeAgeGroup(int contestId, int ageGroupId) {
        deleteFrom("contests_ag")
                .where("contest_id", contestId)
                .where("age_group_id", ageGroupId)
                .execute();
    }

    @Override
    public void updateDuration(int contestId, int ageGroupId, int duration) {
        insertOrUpdateInto("contests_ag")
                .key("contest_id", contestId)
                .key("age_group_id", ageGroupId)
                .value("contest_duration", duration)
                .execute();
    }

    @Override
    public List<AgeGroup> getAllAgeGroups(String lang) {
        return select(AGE_GROUP_FIELDS)
                .from("age_groups")
                .where("lang", lang)
                .getList(JDBCAgeGroupDao::makeAgeGroup);
    }


}
