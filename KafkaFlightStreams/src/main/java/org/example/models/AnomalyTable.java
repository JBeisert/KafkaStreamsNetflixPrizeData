package org.example.models;


public class AnomalyTable {
    private String period;
    private String Title;
    private Integer ratingAmount;
    private Integer ratingAVG;

    public AnomalyTable() {
    }

    public AnomalyTable(String period, String Title, Integer ratingAmount, Integer ratingAVG) {
        this.period = period;
        this.Title = Title;
        this.ratingAmount = ratingAmount;
        this.ratingAVG = ratingAVG;

    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public Integer getRatingAVG() {
        return ratingAVG;
    }

    public void setRatingAVG(Integer ratingAVG) {
        this.ratingAVG = ratingAVG;
    }


    @Override
    public String toString() {
        return "AnomalyRecord{" +
                "period='" + period + '\'' +
                ", Title='" + Title + '\'' +
                ", ratingAmount='" + ratingAmount + '\'' +
                ", ratingAVG='" + ratingAVG + '\'' +
                '}';
    }
}

