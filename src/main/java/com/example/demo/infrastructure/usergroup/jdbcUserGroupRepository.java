package com.example.demo.infrastructure.usergroup;

import com.example.demo.application.usergroup.UserGroupException;
import com.example.demo.domain.usergroup.UserGroup;
import com.example.demo.domain.usergroup.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class jdbcUserGroupRepository implements UserGroupRepository {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    @Transactional
    public void insertUserGroup(UserGroup userGroup) throws DataAccessException{
        try {
            jdbc.update("INSERT INTO user_group(group_id, group_name, created_by) VALUES(?, ?, ?)",
                    userGroup.getUserGroupId(),
                    userGroup.getUserGroupName(),
                    userGroup.getCreatedBy());

            UUID belongingId = UUID.randomUUID();
            jdbc.update("INSERT INTO group_user_belonging(belonging_id, group_id, user_id) VALUES(?, ?, ?)",
                    belongingId.toString(),
                    userGroup.getUserGroupId(),
                    userGroup.getCreatedBy());

        } catch (DataAccessException e) {
            throw new UserGroupException("DB access error occurred when registering new user_group.", e);
        }
    }

    @Override
    public void updateUserGroupName(UserGroup userGroup) throws DataAccessException{
        try {
            jdbc.update("UPDATE user_group SET group_name = ? WHERE group_id = ?",
                    userGroup.getUserGroupName(),
                    userGroup.getUserGroupId());

        } catch (DataAccessException e) {
            throw new UserGroupException("DB access error occurred when updating userGroupName.", e);
        }

    }

}
