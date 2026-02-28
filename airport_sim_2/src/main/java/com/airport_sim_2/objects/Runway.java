package com.airport_sim_2.objects;

public class Runway {

    private final int id;
    private RunwayOpMode mode;
    private RunwayOperationalStatus status;
    private Aircraft currentAircraft;

    public Runway(int id, RunwayOpMode mode, RunwayOperationalStatus status) {
        this.id = id;
        this.mode = mode;
        this.status = status;
        this.currentAircraft = null;
    }

    public int getId() {
        return id;
    }

    public RunwayOpMode getMode() {
        return mode;
    }

    public void setMode(RunwayOpMode mode) {
        this.mode = mode;
    }

    public RunwayOperationalStatus getStatus() {
        return status;
    }

    public void setStatus(RunwayOperationalStatus status) {
        this.status = status;
    }

    public Aircraft getCurrentAircraft() {
        return currentAircraft;
    }

    public boolean isOccupied() {
        return currentAircraft != null;
    }

    public void occupy(Aircraft aircraft) {
        this.currentAircraft = aircraft;
    }

    public void release() {
        this.currentAircraft = null;
    }

    public boolean isAvailableForLanding() {
        return status == RunwayOperationalStatus.AVAILABLE && !isOccupied() && (mode == RunwayOpMode.LANDING || mode == RunwayOpMode.MIXED_MODE);
    }

    public boolean isAvailableForTakeoff() {
        return status == RunwayOperationalStatus.AVAILABLE && !isOccupied() && (mode == RunwayOpMode.TAKE_OFF || mode == RunwayOpMode.MIXED_MODE);
    }
}
