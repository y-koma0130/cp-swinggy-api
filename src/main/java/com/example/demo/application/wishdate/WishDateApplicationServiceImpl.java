package com.example.demo.application.wishdate;

import com.example.demo.Logging;
import com.example.demo.application.usergroup.UserGroupException;
import com.example.demo.application.usergroup.UserGroupQueryService;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.wishdate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishDateApplicationServiceImpl implements WishDateApplicationService {

    @Autowired
    WishDateRepository wishDateRepository;

    @Autowired
    WishDateService wishDateService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserGroupQueryService userGroupQueryService;

    @Autowired
    Logging logger;

    @Override
    public void register(String owner, String date, String userGroupId) throws IllegalArgumentException ,IllegalStateException, WishDateException, UserGroupException, IOException {

        if(!userRepository.exists(owner)) {
            throw new IllegalArgumentException("This owner doesn't exist.");
        }
        if(!userGroupQueryService.exists(userGroupId)) {
            throw new IllegalArgumentException("This groupId doesn't exist.");
        }

        WishDate wishDate = new WishDate(owner, date, userGroupId);

        boolean result = wishDateService.wishDateExists(wishDate);

        if (!result) {
            wishDateRepository.insert(wishDate);
            logger.info("Registered wish date:" + wishDate.getDate().toString());
        } else {
            throw new IllegalStateException("wish date has already existed.");
        }
    }

    @Override
    public List<WishDateModel> getWishDates(Optional<String> from, Optional<String> to, int page, int per, Optional<String> userGroupId) throws IllegalArgumentException {
        Optional<LocalDate> validatedFrom = Optional.empty();
        if(from.isPresent()) {
            LocalDate parsedFrom = parseLocalDate(from.get());
            validatedFrom = Optional.of(parsedFrom);
        }
        Optional<LocalDate> validatedTo = Optional.empty();
        if(to.isPresent()) {
            LocalDate parsedTo = parseLocalDate(to.get());
            validatedTo = Optional.of(parsedTo);
        }

        List<WishDate> wishDateList = null;
        if(userGroupId.isEmpty()) {
            wishDateList = wishDateRepository.selectWishDates(validatedFrom, validatedTo, page, per);
        } else {
            wishDateList = wishDateRepository.selectWishDatesByGroupId(validatedFrom, validatedTo, page, per, userGroupId.get());
        }

        List<WishDateModel> wishDateModelList = wishDateList.stream()
                .map(wishDate -> convertToWishDateModel(wishDate))
                .collect(Collectors.toList());

        return wishDateModelList;

    }

    public int getWishDateCount(Optional<String> from, Optional<String> to, Optional<String> userGroupId) throws IllegalArgumentException {

        Optional<LocalDate> validatedFrom = Optional.empty();
        if(from.isPresent()) {
            LocalDate parsedFrom = parseLocalDate(from.get());
            validatedFrom = Optional.of(parsedFrom);
        }
        Optional<LocalDate> validatedTo = Optional.empty();
        if(to.isPresent()) {
            LocalDate parsedTo = parseLocalDate(to.get());
            validatedTo = Optional.of(parsedTo);
        }

        int count = 0;
        if(userGroupId.isEmpty()) {

            count = wishDateRepository.selectWishDateCount(validatedFrom, validatedTo);
        } else {
            count = wishDateRepository.selectWishDateCountByGroupId(validatedFrom, validatedTo, userGroupId.get());
        }

        return count;
    }

    private LocalDate parseLocalDate(String value) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedValue = LocalDate.parse(value, dtf);
            return parsedValue;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date format error.");
        }
    }

    private WishDateModel convertToWishDateModel(WishDate wishDate) {
        return new WishDateModel(wishDate.getWishDateId(),
                wishDate.getOwner(),
                wishDate.getDate().toString(),
                wishDate.getUserGroupId());
    }

    @Override
    public void deleteWishDate(String wishDateId) throws IllegalStateException, WishDateException {
        wishDateRepository.deleteWishDate(wishDateId);
        logger.info("wish date has been deleted: " + wishDateId);
    }


    @Override
    public void participate(String wishDateId, String participant) throws IllegalStateException, WishDateException {

        if(!userRepository.exists(participant)) {
            throw new IllegalStateException("This participant doesn't exist.");
        }

        Participation participation = new Participation(wishDateId, participant);

        if (wishDateService.isSelfParticipation(participation)) {
            throw new IllegalStateException("This Wish date is the one you registered.");
        }

        if (wishDateService.participationExists(participation)) {
            throw new IllegalStateException("You're already participated in this wish date.");
        }

        wishDateRepository.insertParticipation(participation);
    }

    @Override
    public List<ParticipationModel> getParticipations(String wishDateId, int page, int per) {

        List<Participation> participations = wishDateRepository.selectParticipationsByPage(wishDateId, page, per);

        List<ParticipationModel> participationModels = participations.stream()
                .map(participationModel -> convertToParticipationModel(participationModel))
                .collect(Collectors.toList());

        return participationModels;
    }

    private ParticipationModel convertToParticipationModel(Participation participation) {
        return new ParticipationModel(participation.getParticipationId(),
                participation.getWishDateId(),
                participation.getCreatedAt(),
                participation.getParticipant());
    }

    @Override
    public int getParticipationCount(String wishDateId) {
        int count = wishDateRepository.countParticipations(wishDateId);
        return count;
    }

    @Override
    public void deleteParticipation(String wishDateId, String participationId) throws IllegalArgumentException, WishDateException {
        WishDate wishDate = wishDateRepository.selectById(wishDateId);
        if(wishDate == null) {
            throw new IllegalArgumentException("This wishDatId doesn't exist.");
        }
        wishDateRepository.deleteParticipation(wishDate.getWishDateId(), participationId);
        logger.info("participation has been deleted:" + participationId);
    }

    @Override
    public void postWishDateComment(String wishDateId, String author, String text) throws IllegalStateException, IllegalArgumentException, WishDateException {
        if(!userRepository.exists(author)) {
            throw new IllegalStateException("This author doesn't exist on user table.");
        }

        WishDate wishDate = wishDateRepository.selectById(wishDateId);
        if(wishDate == null) {
            throw new IllegalArgumentException("This wishDatId doesn't exist.");
        }

        WishDateComment wishdateComment = new WishDateComment(wishDate.getWishDateId(), author, text);

        wishDateRepository.insertWishDateComment(wishdateComment);
    }

    private static final int COMMENT_DEFAULT_PAGE = 0;
    private static final int COMMENT_DEFAULT_PER = 100;
    @Override
    public List<WishDateCommentModel> getWishDateComments(String wishDateId, Optional<Integer> page, Optional<Integer> per) throws WishDateException {

        WishDate wishDate = wishDateRepository.selectById(wishDateId);
        if(wishDate == null) {
            throw new IllegalArgumentException("This wishDateId doesn't exist.");
        }

        int pageValue = page.orElse(COMMENT_DEFAULT_PAGE);
        int perValue = per.orElse(COMMENT_DEFAULT_PER);

        List<WishDateComment> wishDateCommentList = wishDateRepository.selectWishDateCommentsByPage(wishDate, pageValue, perValue);

        List<WishDateCommentModel> wishDateCommentModelList = wishDateCommentList.stream()
                .map(wishDateComment -> convertToWishDateCommentModel(wishDateComment))
                .collect(Collectors.toList());

        return wishDateCommentModelList;
    }

    private WishDateCommentModel convertToWishDateCommentModel(WishDateComment wishDateComment) {
        return new WishDateCommentModel(wishDateComment.getWishDateCommentId(),
                wishDateComment.getWishDateId(),
                wishDateComment.getAuthor(),
                wishDateComment.getText(),
                wishDateComment.getCreated_at());
    }

    @Override
    public int getWishDateCommentCount(String wishDateId) {
        int count = wishDateRepository.countWishDateComment(wishDateId);
        return count;
    }

    @Override
    public void deleteWishDateComment(String wishDateId, String wishDateCommentId) throws IllegalArgumentException, WishDateException {
        WishDate wishDate = wishDateRepository.selectById(wishDateId);
        if(wishDate == null) {
            throw new IllegalArgumentException("This wishDatId doesn't exist.");
        }

        wishDateRepository.deleteWishDateComment(wishDate.getWishDateId(), wishDateCommentId);
        logger.info("wishDateCommentId has been deleted:" + wishDateCommentId);
    }

}
