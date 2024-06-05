package org.example.models;


public class Anomaly {
    private String Title;
    private Integer ratingAmount;
    private Integer ratingSum;
    private Integer ratingAVG;

    public Anomaly() {
        this.Title = "";
        this.ratingAmount = 0;
        this.ratingSum = 0;
        this.ratingAVG = 0;
    }
    public Anomaly(String Title, Integer ratingAmount, Integer ratingSum, Integer ratingAVG) {
        this.Title = Title;
        this.ratingAmount = ratingAmount;
        this.ratingSum = ratingSum;
        this.ratingAVG = ratingAVG;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
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

    public Integer getRatingAVG() {
        return ratingAVG;
    }

    public void setRatingAVG(Integer ratingAVG) {
        this.ratingAVG = ratingAVG;
    }
}
