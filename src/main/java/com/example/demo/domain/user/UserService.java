package com.example.demo.domain.user;

import com.example.demo.Logging;
import com.example.demo.application.user.UserCreateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    Logging logger;

    @Autowired
    UserRepository userRepository;

    //screenName, email, telの重複をチェック
    public boolean telExists(User user) {
            List<User> users = null;
            try {
                users = userRepository.selectByTel(user);

            } catch (DataAccessException e) {
                throw new UserCreateException("Failed to access the data source.", e);
            }
            if (users == null) {
                throw new UserCreateException("Unexpected null value was returned from UserRepository.");
            }
            return users.size() > 0;

    }


    public boolean emailExists(User user) {
        List<User> users = null;
        try {
            users = userRepository.selectByEmail(user);

        } catch (DataAccessException e) {
            throw new UserCreateException("Failed to access the data source.", e);
        }
        if (users == null) {
            throw new UserCreateException("Unexpected null value was returned from UserRepository.");
        }
        return users.size() > 0;
    }

    public boolean screenNameExists(User user) {
        List<User> users = null;
        try {
            users = userRepository.selectByScreenName(user);

        } catch (DataAccessException e) {
            throw new UserCreateException("Failed to access the data source.", e);
        }
        if (users == null) {
            throw new UserCreateException("Unexpected null value was returned from UserRepository.");
        }
        return users.size() > 0;
    }
}
