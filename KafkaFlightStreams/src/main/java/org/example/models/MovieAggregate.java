package org.example.models;

import java.util.HashSet;
import java.util.Set;

public class MovieAggregate {
    private String Title;
    private Integer ratingAmount;
    private Integer ratingSum;
    private Set<String> uniqueUsers;
    private Integer uniqueUsersCount;

    public MovieAggregate() {
        this.ratingAmount = 0;
        this.ratingSum = 0;
        this.uniqueUsers = new HashSet<>();
        this.uniqueUsersCount = 0;
        this.Title = "";
    }

    public MovieAggregate(String Title, Integer ratingAmount, Integer ratingSum, Set<String> uniqueUsers, Integer uniqueUsersCount) {
        this.Title = Title;
        this.ratingAmount = ratingAmount;
        this.ratingSum = ratingSum;
        this.uniqueUsers = uniqueUsers;
        this.uniqueUsersCount = uniqueUsersCount;
    }

    public Integer getRatingAmount() {
        return ratingAmount;
    }

    public void setRatingAmount(Integer ratingAmount) {
        this.ratingAmount = ratingAmount;
    }

    public Integer getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(Integer ratingSum) {
        this.ratingSum = ratingSum;
    }

    public Set<String> getUniqueUsers() {
        return uniqueUsers;
    }

    public void setUniqueUsers(Set<String> uniqueUsers) {
        this.uniqueUsers = uniqueUsers;
    }

    public Integer getUniqueUsersCount() {
        return uniqueUsersCount;
    }

    public void setUniqueUsersCount(Integer uniqueUsersCount) {
        this.uniqueUsersCount = uniqueUsersCount;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    @Override
    public String toString() {
        return "MovieAggregate{" +
                "Title=" + Title +
                ", ratingAmount=" + ratingAmount +
                ", ratingSum=" + ratingSum +
                ", uniqueUsersCount=" + uniqueUsersCount +
                '}';
    }

}
