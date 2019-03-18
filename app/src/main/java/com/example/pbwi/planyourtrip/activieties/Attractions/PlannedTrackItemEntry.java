package com.example.pbwi.planyourtrip.activieties.Attractions;

public class PlannedTrackItemEntry {
    private GoogleAttractionEntry startAttraction;
    private GoogleAttractionEntry endAttraction;

    public PlannedTrackItemEntry(GoogleAttractionEntry startAttraction, GoogleAttractionEntry endAttraction) {
        this.startAttraction = startAttraction;
        this.endAttraction = endAttraction;
    }

    public GoogleAttractionEntry getStartAttraction() {
        return startAttraction;
    }

    public GoogleAttractionEntry getEndAttraction() {
        return endAttraction;
    }
}
