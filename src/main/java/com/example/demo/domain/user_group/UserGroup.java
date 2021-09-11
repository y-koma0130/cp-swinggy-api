package com.example.demo.domain.user_group;

public class UserGroup {

    private UserGroupId userGroupId;
    private UserGroupName userGroupName;
    private String createdBy;

    public UserGroup(String userGroupName, String createdBy) throws IllegalArgumentException{
        this.userGroupName = new UserGroupName(userGroupName);
        this.userGroupId = new UserGroupId();
        this.createdBy = createdBy;
    }

    public String getUserGroupId() {return this.userGroupId.getValue();}
    public String getUserGroupName() {return this.userGroupName.getValue();}
    public String getCreatedBy() {return this.createdBy;}

}
