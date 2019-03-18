package com.example.pbwi.planyourtrip.activieties.Attractions;

public class GoogleAttractionEntry {
    private String name;
    private double lat;
    private double lng;
    private double rate;
    private int numberOfRatings;
    private String placeID;
    private String address;
    private boolean checked = false;
    private boolean isAddedToVisit = false;
    private int index;

    public GoogleAttractionEntry() {
    }

    public GoogleAttractionEntry(String name, double lat, double lng, double rate, String placeID, String address, int numberOfRatings) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.rate = rate;
        this.placeID = placeID;
        this.address = address;
        this.numberOfRatings = numberOfRatings;
    }

    public GoogleAttractionEntry(String placeID, String address) {
        this.placeID = placeID;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getRate() {
        return rate;
    }


    public String getPlaceID() {
        return placeID;
    }


    public String getAddress() {
        return address;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
            checked = !checked;
    }


    @Override
    public String toString() {
        return "GoogleAttractionEntry{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", rate=" + rate +
                ", placeID='" + placeID + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAddedToVisit() {
        return isAddedToVisit;
    }

    public void setAddedToVisit(boolean addedToVisit) {
        isAddedToVisit = addedToVisit;
    }
}
