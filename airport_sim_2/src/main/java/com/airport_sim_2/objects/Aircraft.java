package com.airport_sim_2.objects;

public class Aircraft {
    private static long sequenceCounter = 0;
    // ensures FIFO ordering
    private final long sequenceNumber;
    private final String callsign;
    private final String operator;
    private final String origin;
    private final String destination;
    private final float groundSpeed;
    private final int altitude;

    private float fuel; // litres of fuel remaining
    private AircraftStatus status;
    private final Double scheduled_ts; // schedule timestamp for processing in seconds / ticks
    private Double process_ts; // actual timestamp for when the aircraft is processed
    
    public Aircraft(String callsign,
                    String operator, 
                    String origin, 
                    String destination, 
                    float groundSpeed, 
                    float fuel, 
                    int altitude, 
                    AircraftStatus status, 
                    Double scheduled_processing_timestamp

    ) {
        this.sequenceNumber = sequenceCounter++;
        this.callsign = callsign;
        this.operator = operator;
        this.origin = origin;
        this.destination = destination;
        this.groundSpeed = groundSpeed; //units of fuel per second/tick
        this.altitude = altitude;
        this.fuel = fuel;
        this.status = status;
        this.scheduled_ts = scheduled_processing_timestamp;
    }

    public Double getScheduledProcessingTimestamp() { return this.scheduled_ts; }


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

    public int getAltitude() {
        return this.altitude;
    }

    public float getFuel() {
        return this.fuel;
    }

    public AircraftStatus getStatus() {
        return this.status;
    }

    public Double getScheduledTime() {
        return this.scheduled_ts;
    }

    public Double getActualTime() {
        return this.process_ts;
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

    public void setActualTime(Double process_ts) {
        this.process_ts = process_ts;
    }

    /**
     * Return the amount of delay from the original event processing time.
     */
    public Double getDelayTicks() {
        if (process_ts == null) {
            return 0.0d;
        }
        return process_ts - scheduled_ts;
    }

    @Override
    public String toString() {
        return callsign + " (" + status + ")";
    }
}
