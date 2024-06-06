package org.example.models;

import java.io.Serializable;

public class MovieRatingInfoJoined implements Serializable {
    private String dateString;
    private String film_id;
    private String user_id;
    private String rate;
    private String Title;

    public MovieRatingInfoJoined() {
    }

    public MovieRatingInfoJoined(String dateString, String film_id, String user_id, String rate, String Title) {
        this.dateString = dateString;
        this.film_id = film_id;
        this.user_id = user_id;
        this.rate = rate;
        this.Title = Title;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s", dateString, film_id, user_id, rate, Title);
    }

    public String getDate() {
        return dateString;
    }

    public void setDate(String dateString) {
        this.dateString = dateString;
    }

    public String getFilm_id() {return film_id;}

    public void setFilm_id(String film_id) {
        this.film_id = film_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String rate) {
        this.Title = Title;
    }

}
