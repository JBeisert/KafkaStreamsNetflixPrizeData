package org.example.models;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieInfo implements Serializable {
    private static final String LOG_ENTRY_PATTERN = "^([^,]+),([^,]+),([^,]+)$";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);

    private String ID;
    private String Year;
    private String Title;

    public MovieInfo() {
    }

    public MovieInfo(String ID, String Year, String Title) {
        this.ID = ID;
        this.Year = Year;
        this.Title = Title;
    }

    public static MovieInfo parseFromLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (!matcher.find()) {
            throw new RuntimeException("Error parsing line: " + line);
        }

        MovieInfo movieInfo = new MovieInfo(
                matcher.group(1),
                matcher.group(2),
                matcher.group(3)
        );


        return movieInfo;
    }

    public static boolean isLineCorrect(String line) {

        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            return true;
        }

        return false;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", ID, Year, Title);
    }
}


