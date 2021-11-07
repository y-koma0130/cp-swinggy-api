package com.example.demo.application.usergroupthread;

import java.util.Optional;

public interface UserGroupThreadApplicationService {

    public UserGroupThreadListQueryModel getUserGroupThreads(String userGroupId, Optional<Integer> page, Optional<Integer> per) throws IllegalArgumentException;

}