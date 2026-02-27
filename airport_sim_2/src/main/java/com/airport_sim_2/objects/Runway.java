package com.airport_sim_2.objects;

public class Runway {
    private RunwayOpMode mode;
    private RunwayOperationalStatus status;
    private Aircraft currenAircraft;

    public boolean isOccupied() {
        return currenAircraft != null;
    }
 
    public boolean isAvailableForLanding() {
        return status == RunwayOperationalStatus.AVAILABLE && !isOccupied() && (mode == RunwayOpMode.LANDING || mode == RunwayOpMode.MIXED_MODE);
    }
}
