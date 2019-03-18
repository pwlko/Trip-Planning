package com.example.pbwi.planyourtrip.activieties.Tracks;



import java.util.HashMap;

/**
 * Created by komputer-prywatny on 16.01.2018.
 */

public class SavedTrackEntry {
    private String name;
    private String description;
    private long timeStart;
    private long timeEnd;
    private String startPointID;
    private String endPointID;
    private String trackID;
    private int savedPointsNumber;
    private HashMap<String, LocationEntry> Locations;

    private boolean checked = false;



    public SavedTrackEntry(){

    }

    public SavedTrackEntry(int savedPointsNumber, String name, String description, long timeStart, long timeEnd, HashMap<String, LocationEntry> Locations) {
        this.name = name;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.savedPointsNumber = savedPointsNumber;
        this.Locations = Locations;
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

    public void toggleChecked() {
        checked = !checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStartPointID() {
        return startPointID;
    }

    public void setStartPointID(String startPointID) {
        this.startPointID = startPointID;
    }

    public String getEndPointID() {
        return endPointID;
    }

    public void setEndPointID(String endPointID) {
        this.endPointID = endPointID;
    }

    public int getSavedPointsNumber() {
        return savedPointsNumber;
    }

    public void setSavedPointsNumber(int savedPointsNumber) {
        this.savedPointsNumber = savedPointsNumber;
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public HashMap<String, LocationEntry> getLocations() {
        return Locations;
    }

    public void setLocations(HashMap<String, LocationEntry> locations) {
        Locations = locations;
    }
}
