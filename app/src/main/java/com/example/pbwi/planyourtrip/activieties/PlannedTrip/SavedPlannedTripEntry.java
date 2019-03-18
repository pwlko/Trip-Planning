package com.example.pbwi.planyourtrip.activieties.PlannedTrip;

import java.util.HashMap;

public class SavedPlannedTripEntry {

    private String name;
    private String targetLocation;
    private String tripID;
    private int numberOfDays;
    private int numberOfPlacesToVistit;
    private long creationDate;
    private HashMap<String,HashMap<String,String>> Trip;
    private String tripHelpID;
    private String targetAddress;
    private boolean checked = false;

    public SavedPlannedTripEntry() {
    }

    public SavedPlannedTripEntry(String name, String targetLocation, String tripID, int numberOfDays, int numberOfPlacesToVistit, long creationDate, HashMap<String,HashMap<String,String>> Trip) {
        this.name = name;
        this.targetLocation = targetLocation;
        this.tripID = tripID;
        this.numberOfDays = numberOfDays;
        this.numberOfPlacesToVistit = numberOfPlacesToVistit;
        this.creationDate = creationDate;
        this.Trip = Trip;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public int getNumberOfPlacesToVistit() {
        return numberOfPlacesToVistit;
    }

    public void setNumberOfPlacesToVistit(int numberOfPlacesToVistit) {
        this.numberOfPlacesToVistit = numberOfPlacesToVistit;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public HashMap<String,HashMap<String,String>> getTrip() {
        return Trip;
    }

    public void setTrip(HashMap<String,HashMap<String,String>> trip) {
        this.Trip = trip;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }



    public String getTripHelpID() {
        return tripHelpID;
    }

    public void setTripHelpID(String tripHelpID) {
        this.tripHelpID = tripHelpID;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }
}
