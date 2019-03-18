package com.example.pbwi.planyourtrip.activieties.SavedPlaces;

/**
 * Created by komputer-prywatny on 16.01.2018.
 */

public class SavedPlaceEntry {
    private String name;
    private String description;
    private long date;
    private double lat;
    private double lng;
    private String trackID;
    private String placeID;
    private boolean checked = false;

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public SavedPlaceEntry(){

    }

    public SavedPlaceEntry(String number, String description) {

        this.name = number;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String number) {
        this.name = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void toggleChecked() {
        checked = !checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
