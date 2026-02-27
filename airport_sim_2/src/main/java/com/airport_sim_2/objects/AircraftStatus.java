package com.airport_sim_2.objects;

public enum AircraftStatus {
    // emergency types aren't ranked in the spec so im using these numbers for simplicity
    NORMAL("Normal", 0),
    FUEL("Fuel below 10%.", 2),
    MECH_FAILURE("Mechanical Failure", 1),
    PASSENGER_HEALTH("Passenger(s) poor health.", 1);

    private final String status_str;
    private final int priority;

    AircraftStatus(String status_str, int priority) {
        this.status_str = status_str;
        this.priority = priority;
    }
        
    public String getStatus() {
        return this.status_str;
    }

    public int getPriority() {
        return this.priority;
    }
}

