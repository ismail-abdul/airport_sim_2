package com.airport_sim_2.objects;

public enum RunwayOpMode {

    LANDING("Landing"),
    TAKE_OFF("Take-off"),
    MIXED_MODE("Mixed Mode");

    private final String description;

    RunwayOpMode(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
