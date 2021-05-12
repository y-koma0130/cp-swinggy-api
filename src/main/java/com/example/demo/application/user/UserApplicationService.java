package com.example.demo.application.user;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserApplicationService {

    public void create(String userId, String firstName, String lastName, String screenName, Optional<MultipartFile> profileImage, String email, String tel) throws UserCreateException, IllegalArgumentException;

    public UserModel get(String userId) throws IllegalArgumentException;
}
