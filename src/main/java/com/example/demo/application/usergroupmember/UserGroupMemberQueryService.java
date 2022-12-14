package com.example.demo.application.usergroupmember;


public interface UserGroupMemberQueryService {

    public UserGroupMemberListQueryModel selectUserGroupMembersByUserGroupId(String userGroupId, int page, int per);

    public UserGroupMemberQueryModel selectUserGroupMember(String groupId, String userId);

}
