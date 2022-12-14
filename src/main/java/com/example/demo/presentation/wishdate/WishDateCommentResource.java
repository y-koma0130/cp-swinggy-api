package com.example.demo.presentation.wishdate;

import com.example.demo.application.wishdate.WishDateCommentModel;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.text.SimpleDateFormat;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WishDateCommentResource {

    private String id;
    private String wishDateId;
    private String author;
    private String text;
    private String createdAt;

    public WishDateCommentResource(WishDateCommentModel wishDateCommentModel) {
        this.id = wishDateCommentModel.getWishDateCommentId();
        this.wishDateId = wishDateCommentModel.getWishDateId();
        this.author = wishDateCommentModel.getAuthor();
        this.text = wishDateCommentModel.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createdAt = sdf.format(wishDateCommentModel.getCreatedAt());
    }

    public String getId() {return this.id;}
    public String getWishDateId() {return this.wishDateId;}
    public String getAuthor() {return this.author;}
    public String getText() {return this.text;}
    public String getCreatedAt() {return this.createdAt;}

}
