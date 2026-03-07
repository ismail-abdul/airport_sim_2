package com.airport_sim_2.objects;

/**
 * Enum for describing the current purpose of a runway; What is the runway supposed to be used for?
 */
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
