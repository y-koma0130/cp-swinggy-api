package com.example.demo.infrastructure.wish_date;

import com.example.demo.application.wish_date.ParticipateWishDateException;
import com.example.demo.application.wish_date.WishDateRegisterException;
import com.example.demo.domain.wish_date.Participation;
import com.example.demo.domain.wish_date.WishDate;
import com.example.demo.domain.wish_date.WishDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class JdbcWishDateRepository implements WishDateRepository {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    @Transactional
    public void insert(WishDate wishDate) throws IOException, WishDateRegisterException {

        try {

            jdbc.update("insert into wish_date(wish_date_id, owner, wish_date) values(?, ?, ?)", wishDate.getWishDateId(), wishDate.getOwner(), wishDate.getDate());

        } catch (DataAccessException e) {
            throw new WishDateRegisterException("DB access error occurred when inserting wish date.", e);
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> select(String owner, LocalDate date) throws WishDateRegisterException {
        try {
            List<Map<String, Object>> wishDateList = jdbc.queryForList("select * from wish_date where owner = ? and wish_date = ?", owner, date.toString());

            return wishDateList;

        } catch (DataAccessException e) {
            throw new WishDateRegisterException("DB access error occurred while checking if the same wish date exists.", e);
        }
    }

    @Override
    @Transactional
    public List<WishDate> selectAll() {

        List<Map<String, Object>> wishDateDate = jdbc.queryForList("select * from wish_date");

        List<WishDate> wishDateList = wishDateDate.stream()
                .map(wishDate -> convertToWishDate(wishDate))
                .collect(Collectors.toList());

        return wishDateList;
    }

    private WishDate convertToWishDate(Map<String, Object> wishDate) {

        return new WishDate(
                (String) wishDate.get("wish_date_id"),
                (String) wishDate.get("owner"),
                wishDate.get("wish_date").toString());
    }

    @Override
    @Transactional
    public void insertIntoParticipation(Participation participation) throws ParticipateWishDateException{

        try {
            jdbc.update("insert into participation(participation_id, wish_date_id, created_at, participant) values(?, ?, ?, ?)",
                    participation.getParticipationId(),
                    participation.getWishDateId(),
                    participation.getDate(),
                    participation.getParticipant());
        } catch (DataAccessException e) {
            throw new ParticipateWishDateException("DB access error occurred when insert into Participation.", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> selectById(String wishDateId) throws DataAccessException {

        Map<String, Object> wishDateData = jdbc.queryForMap(
                "select * from wish_date where wish_date_id = ?",
                wishDateId);

        return wishDateData;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> participationExists(String wishDateId, String participant) throws DataAccessException {

        List<Map<String, Object>> participations = jdbc.queryForList(
                "select * from participation where wish_date_id = ? and participant = ?",
                wishDateId, participant);

        return participations;
    }

    @Override
    @Transactional
    public List<Participation> selectParticipation(String wishDateId, int page, int per) {

        int offset = 0;
        if(page > 0) {
            offset = page * per;
        }

        List<Map<String, Object>> participationData = jdbc.queryForList(
                "select * from participation where wish_date_id = ? order by created_at desc limit ? offset ?",
                wishDateId, per, offset);

        List<Participation> participations = participationData.stream()
                .map(participation -> convertToParticipation(participation))
                .collect(Collectors.toList());

        return participations;
    }

    private Participation convertToParticipation(Map<String, Object> participation) {

        System.out.println(participation.get("created_at"));
        return new Participation((String)participation.get("participation_id"),
                (String)participation.get("wish_date_id"),
                participation.get("created_at").toString(),
                (String)participation.get("participant"));
    }

    @Override
    @Transactional
    public int countParticipations(String wishDateId) {

        Integer count = jdbc.queryForObject("select count(*) from participation where wish_date_id = ?", Integer.class, wishDateId);

        return count;
    }

}