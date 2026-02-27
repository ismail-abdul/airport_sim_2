package com.airport_sim_2.objects;

public enum RunwayOperationalStatus {

    AVAILABLE("Available"),
    INSPECTION("Inspection"),
    SNOW_CLEARANCE("Snow Clearance"),
    EQUIPMENT_FAILURE("Equipment Failure");

    private final String description;

    RunwayOperationalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
