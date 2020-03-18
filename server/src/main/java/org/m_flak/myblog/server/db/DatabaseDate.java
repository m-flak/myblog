package org.m_flak.myblog.server.db;

import java.lang.Comparable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DatabaseDate implements Comparable<DatabaseDate> {
    public static final int AFTER = 1;
    public static final int BEFORE = -1;
    public static final int EQUAL = 0;

    public LocalDateTime dateTime;
    private final DateTimeFormatter timeFormat;

    public DatabaseDate() {
        timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        dateTime = LocalDateTime.now();
    }

    public DatabaseDate(String dateTimeString) {
        timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        try {
            dateTime = LocalDateTime.parse(dateTimeString, timeFormat);
        }
        catch (DateTimeParseException de) {
            /* If a fraction of a second is missing from the input, add it. */
            dateTime = LocalDateTime.parse(new String(dateTimeString).concat(".0"), timeFormat);
        }
    }

    public DatabaseDate(DatabaseDate other) {
        this.dateTime = other.dateTime;
        this.timeFormat = other.timeFormat;
    }

    public void addDays(long days) {
        dateTime = dateTime.plusDays(days);
    }

    public boolean isMonth(int month) {
        return dateTime.getMonth().getValue() == month;
    }

    public boolean isYear(int year) {
        return dateTime.getYear() == year;
    }

    public boolean isDay(int day) {
        return dateTime.getDayOfMonth() == day;
    }

    @Override
    public String toString() {
        return dateTime.format(timeFormat);
    }

    @Override
    public int compareTo(DatabaseDate other) {
        int difference = DatabaseDate.AFTER;

        if (dateTime.isAfter(other.dateTime)) {
            difference = DatabaseDate.AFTER;
        }
        else if (dateTime.isBefore(other.dateTime)) {
            difference = DatabaseDate.BEFORE;
        }
        else if (dateTime.isEqual(other.dateTime)) {
            difference = DatabaseDate.EQUAL;
        }

        return difference;
    }
}
