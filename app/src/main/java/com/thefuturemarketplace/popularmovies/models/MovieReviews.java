package com.thefuturemarketplace.popularmovies.models;

/**
 * Created by imkiran
 */

public class MovieReviews {
    private String author;
    private String comment;

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {

        return author;
    }

    public String getComment() {
        return comment;
    }
}
