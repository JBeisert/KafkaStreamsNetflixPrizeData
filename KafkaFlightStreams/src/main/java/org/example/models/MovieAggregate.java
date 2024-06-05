package org.example.models;

import java.util.HashSet;
import java.util.Set;

public class MovieAggregate {
    private Integer ratingAmount;
    private Integer ratingSum;
    private Set<String> uniqueUsers;

    public MovieAggregate() {
        this.ratingAmount = 0;
        this.ratingSum = 0;
        this.uniqueUsers = new HashSet<>();
    }

    public MovieAggregate(Integer ratingAmount, Integer ratingSum, Set<String> uniqueUsers) {
        this.ratingAmount = ratingAmount;
        this.ratingSum = ratingSum;
        this.uniqueUsers = uniqueUsers;
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

    @Override
    public String toString() {
        return "MovieAggregate{" +
                "ratingAmount=" + ratingAmount +
                ", ratingSum=" + ratingSum +
                ", uniqueUsers=" + uniqueUsers.size() +
                '}';
    }

}
