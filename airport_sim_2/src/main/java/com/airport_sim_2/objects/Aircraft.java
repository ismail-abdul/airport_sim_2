package com.airport_sim_2.objects;
import java.time.LocalDateTime;

public class Aircraft {
    private static long sequenceCounter = 0;
    // ensures FIFO ordering
    private final long sequenceNumber;
    private final String callsign;
    private final String operator;
    private final String origin;
    private final String destination;
    private final float groundSpeed;
    private final float altitude;
    // minutes of fuel remaining
    private float fuel;                  
    private AircraftStatus status;
    private final LocalDateTime scheduledTime;
    private LocalDateTime actualTime;
    
    public Aircraft(String callsign, String operator, String origin, String destination, float groundSpeed, float altitude, float fuel, AircraftStatus status, LocalDateTime scheduledTime) {
        this.sequenceNumber = sequenceCounter++;
        this.callsign = callsign;
        this.operator = operator;
        this.origin = origin;
        this.destination = destination;
        this.groundSpeed = groundSpeed;
        this.altitude = altitude;
        this.fuel = fuel;
        this.status = status;
        this.scheduledTime = scheduledTime;
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    public String getCallsign() {
        return this.callsign;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getOrigin() {
        return this.origin;
    }

    public String getDestination() {
        return this.destination;
    }

    public float getGroundSpeed() {
        return this.groundSpeed;
    }

    public float getAltitude() {
        return this.altitude;
    }

    public float getFuel() {
        return this.fuel;
    }

    public AircraftStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getScheduledTime() {
        return this.scheduledTime;
    }

    public LocalDateTime getActualTime() {
        return this.actualTime;
    }

    public void reduceFuel(float amount) {
        fuel -= amount;
        if (fuel < 0) {
            fuel = 0;
        }

        // auto update emergency status if the fuel is critical
        if (fuel <= 10 && status == AircraftStatus.NORMAL) {
            status = AircraftStatus.FUEL;
        }
    }

    public void setActualTime(LocalDateTime actualTime) {
        this.actualTime = actualTime;
    }

    public long getDelayMinutes() {
        if (actualTime == null) {
            return 0;
        }
        return java.time.Duration.between(scheduledTime, actualTime).toMinutes();
    }

    @Override
    public String toString() {
        return callsign + " (" + status + ")";
    }
}
