package com.example.demo.domain.wish_date;

import com.example.demo.application.wish_date.WishDateException;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WishDateRepository {

    public void insert(WishDate wishDate) throws WishDateException;

    public List<WishDate> selectWishDateByDate(LocalDate date) throws WishDateException, IOException;

    public List<WishDate> selectWishDatesByPage(Optional<LocalDate> from,Optional<LocalDate> to, int page, int per);

    public int selectWishDateCount(Optional<LocalDate> from,Optional<LocalDate> to);

    public void insertParticipation(Participation participation) throws WishDateException;

    public WishDate selectById(String wishDateId) throws DataAccessException;

    public void deleteWishDate(String wishDateId) throws WishDateException, IllegalArgumentException;

    public List<Participation> selectParticipation(String wishDateId, String participant) throws DataAccessException;

    public List<Participation> selectParticipationsByPage(String wishDateId, int page, int per) throws IllegalArgumentException;

    public int countParticipations(String wishDateId);

    public void deleteParticipation(String wishDateId, String participationId) throws IllegalArgumentException;

    public void insertWishDateComment(WishDateComment wishDateComment) throws WishDateException;

    public List<WishDateComment> selectWishDateCommentsByPage(WishDate wishDate, int page, int per);

    public int countWishDateComment(String wishDateId);
}