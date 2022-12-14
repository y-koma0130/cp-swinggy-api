package com.example.demo.application.user;

import com.example.demo.Logging;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {

    @Autowired
    private Logging logger;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Value("${default.profile.image.url}")
    private URL defaultProfileImageUrl;


    @Override
    public void create(String userId, String firstName, String lastName, String screenName, Optional<MultipartFile> profileImage, String email, Optional<String> tel)
            throws UserCreateException, IllegalStateException, IllegalArgumentException {

        User user = new User(userId, firstName, lastName, screenName, email, tel, defaultProfileImageUrl);

        //ドメインサービス 重複チェッククラス
        if (user.getTel().isPresent() && userService.telExists(user)) {
            throw new IllegalStateException("This tel number has already existed");
        }
        if (userService.emailExists(user)) {
            throw new IllegalStateException(("This email address has already existed"));
        }
        if (userService.screenNameExists(user)) {
            throw new IllegalStateException("This screen name hss already existed");
        }

        logger.debug("insert user data into db & upload profileImage");
        userRepository.insert(user, profileImage);

    }


    @Override
    public UserModel get(String userId) throws UserCreateException, IllegalStateException {

        User user = userRepository.select(userId);

        UserModel userModel = convertToUserModel(user);

        return userModel;
    }


    private UserModel convertToUserModel(User user) {

        return new UserModel(user.getUserId(),
                user.getFirstName().getValue(),
                user.getLastName().getValue(),
                user.getScreenName().getValue(),
                user.getEmail().getValue(),
                user.getTel().isPresent() ? Optional.of(user.getTel().get().getValue()) : Optional.empty(),
                user.getProfileImageURL().getValue());
    }

    @Override
    public List<UserModel> getUsers(int page, int per, Optional<String> userGroupId) throws UserCreateException{
        List<User> users = null;

        if(userGroupId.isEmpty()) {
            users = userRepository.selectUsers(page, per);
        } else {
            users = userRepository.selectUsersByGroupId(page, per, userGroupId.get());
        }

        List<UserModel> userModels = users.stream()
                .map(userModel -> convertToUserModel(userModel))
                .collect(Collectors.toList());

        return userModels;
    }
    @Override
    public int getCount(Optional<String> userGroupId) {
        int count;
        if(userGroupId.isEmpty()) {
            count = userRepository.selectCount();
        } else {
            count = userRepository.selectCountByGroupId(userGroupId.get());
        }

        return count;
    }

    @Override
    public int getCountByScreenName(String screenName) throws UserCreateException{

        int count = userRepository.selectCountByScreenName(screenName);

        return count;
    }
}
