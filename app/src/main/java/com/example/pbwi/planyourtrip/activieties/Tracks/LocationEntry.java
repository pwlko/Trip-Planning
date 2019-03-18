package com.example.pbwi.planyourtrip.activieties.Tracks;

import java.io.Serializable;

public class LocationEntry implements Serializable {
    private double latitude;
    private double longitude;
    private long time;
    private boolean pause;
    private String savedPlaceID;

    public LocationEntry() {
    }

    public LocationEntry(double latitude, double longitude, long time, boolean pause, String savedPlaceID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.pause = pause;
        this.savedPlaceID = savedPlaceID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public String getSavedPlaceID() {
        return savedPlaceID;
    }

    public void setSavedPlaceID(String savedPlaceID) {
        this.savedPlaceID = savedPlaceID;
    }
}
