package com.app.triponeer;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;


public class Trip {
    private String name;
    private String Description;
    private String sourceName;
    private String destinationName;
    private String sourceAddress;
    private String destinationAddress;
    private String status;
    private String type;
    private double distance;
    private double sourceLat;
    private double sourceLong;
    private double destLat;
    private double destLong;
    private String date;
    private String time;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    private String roundDate;
    private String roundTime;
    private int roundYear;
    private int roundMonth;
    private int roundDay;
    private int roundHour;
    private int roundMinute;
    private boolean isRoundDone;

    private String repeatPattern;
    private ArrayList<String> repeatDays;
    private ArrayList<String> notes;

    public Trip() {
        name = "";
        Description = "";
        sourceAddress = "";
        destinationAddress = "";
        sourceName = "";
        destinationName = "";
        status = "Upcoming";
        sourceLat = 0;
        sourceLong = 0;
        destLat = 0;
        destLong = 0;
        type = "One Way";
        date = "";
        time = "";
        day = 0;
        month = 0;
        year = 0;
        hour = 0;
        minute = 0;
        roundDate = "";
        roundTime = "";
        roundYear = 0;
        roundMonth = 0;
        roundDay = 0;
        roundHour = 0;
        roundMinute = 0;
        isRoundDone = false;
        repeatPattern = "";
        distance = 0;
        notes = new ArrayList<String>();
        repeatDays = new ArrayList<String>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setRepeatPattern(String repeatPattern) {
        this.repeatPattern = repeatPattern;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public void setRepeatDays(ArrayList<String> repeatDays) {
        this.repeatDays = repeatDays;
    }

    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void setSource(String sourceName, String sourceAddress, double sourceLat, double sourceLong) {
        this.sourceName = sourceName;
        this.sourceAddress = sourceAddress;
        this.sourceLat = sourceLat;
        this.sourceLong = sourceLong;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestination(String destinationName, String destinationAddress, double destLat, double destLong) {
        this.destinationName = destinationName;
        this.destinationAddress = destinationAddress;
        this.destLat = destLat;
        this.destLong = destLong;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return Description;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public double getSourceLat() {
        return sourceLat;
    }

    public double getSourceLong() {
        return sourceLong;
    }

    public double getDestLat() {
        return destLat;
    }

    public double getDestLong() {
        return destLong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRepeatPattern() {
        return repeatPattern;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public ArrayList<String> getRepeatDays() {
        return repeatDays;
    }

    public String getRoundDate() {
        return roundDate;
    }

    public void setRoundDate(String roundDate) {
        this.roundDate = roundDate;
    }

    public String getRoundTime() {
        return roundTime;
    }

    public void setRoundTime(String roundTime) {
        this.roundTime = roundTime;
    }

    public int getRoundYear() {
        return roundYear;
    }

    public void setRoundYear(int roundYear) {
        this.roundYear = roundYear;
    }

    public int getRoundMonth() {
        return roundMonth;
    }

    public void setRoundMonth(int roundMonth) {
        this.roundMonth = roundMonth;
    }

    public int getRoundDay() {
        return roundDay;
    }

    public void setRoundDay(int roundDay) {
        this.roundDay = roundDay;
    }

    public int getRoundHour() {
        return roundHour;
    }

    public void setRoundHour(int roundHour) {
        this.roundHour = roundHour;
    }

    public int getRoundMinute() {
        return roundMinute;
    }

    public void setRoundMinute(int roundMinute) {
        this.roundMinute = roundMinute;
    }

    public boolean isRoundDone() {
        return isRoundDone;
    }

    public void setRoundDone(boolean roundDone) {
        isRoundDone = roundDone;
    }

    public void swapRound() {
        // swap location names
        String temp;
        temp = sourceName;
        sourceName = destinationName;
        destinationName = temp;

        // swap location address
        temp = sourceAddress;
        sourceAddress = destinationAddress;
        destinationAddress = temp;

        // swap latitudes
        double temp1;
        temp1 = sourceLat;
        sourceLat = destLat;
        destLat = temp1;

        // swap longitudes
        temp1 = sourceLong;
        sourceLong = destLong;
        destLong = temp1;

        // round to normal
        time = roundTime;
        date = roundDate;
        hour = roundHour;
        minute = roundMinute;
        year = roundYear;
        month = roundMonth;
        day = roundDay;

        isRoundDone = true;


    }
}
