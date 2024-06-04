package org.example.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieRating implements Serializable {
    private static final String LOG_ENTRY_PATTERN = "^([^,]+),([^,]+),([^,]+),([^,]+)$";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);
    private String dateString;
    private String film_id;
    private String user_id;
    private String rate;

    public MovieRating(String dateString, String film_id, String user_id, String rate) {
        this.dateString = dateString;
        this.film_id = film_id;
        this.user_id = user_id;
        this.rate = rate;
    }

    public static MovieRating parseFromLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (!matcher.find()) {
            throw new RuntimeException("Error parsing line: " + line);
        }

        MovieRating movieRating = new MovieRating(
                matcher.group(1),
                matcher.group(2),
                matcher.group(3),
                matcher.group(4)
        );

        //System.out.println(movieRating.toString());

        return movieRating;
    }

    public static boolean isLineCorrect(String line) {
        //System.out.println(line);

        if (line.contains("date")) {
            return false;
        };

        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            //System.out.println("CORRECT LINE");
            return true;
        }

        //System.out.println("INCORRECT LINE");
        return false;
    }

    public static String parseOrderColumns(String orderColumn) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(orderColumn, formatter);
        return localDate.toString();
    }

    public static MovieRating parseFromLogLine(String logline) {
        String[] m = logline.split(",");
        return new MovieRating(m[0], m[1], m[2], m[3]);
    }

    public static boolean lineIsCorrect(String logline) {
        String[] m = logline.split(",");
        return m.length == 4 && m[0].contains("-");
    }

    public long extractTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date;

        try {
            date = sdf.parse(this.dateString);
            return date.getTime();
        } catch (ParseException e) {
            System.out.println("ORDER DATE PARSE EXCEPTION");
            return -1;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s ", dateString, film_id, user_id, rate);
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

}


