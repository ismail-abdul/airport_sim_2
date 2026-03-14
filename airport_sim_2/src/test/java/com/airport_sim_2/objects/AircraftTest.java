package com.airport_sim_2.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Phase 1: Unit tests for Aircraft and AircraftStatus.
 * Updated to match the new Double (Tick) timing system.
 */
public class AircraftTest {

    private Aircraft createDummyPlane(float fuel, AircraftStatus status, Double scheduledTime) {
        // Constructor Order: callsign, operator, origin, dest, speed, fuel, altitude, status, time
        return new Aircraft("TEST01", "OP", "ORG", "DST", 400.0f, fuel, 3000, status, scheduledTime);
    }

    @Test
    public void aircraftInitializationTest() {
        Double scheduledTime = 100.5;
        Aircraft aircraft = createDummyPlane(200.0f, AircraftStatus.NORMAL, scheduledTime);
        
        assertEquals("TEST01", aircraft.getCallsign());
        assertEquals("OP", aircraft.getOperator());
        assertEquals("ORG", aircraft.getOrigin());
        assertEquals("DST", aircraft.getDestination());
        assertEquals(400.0f, aircraft.getGroundSpeed(), 0.001f);
        assertEquals(200.0f, aircraft.getFuel(), 0.001f);
        assertEquals(3000, aircraft.getAltitude());
        assertEquals(AircraftStatus.NORMAL, aircraft.getStatus());
        assertEquals(scheduledTime, aircraft.getScheduledTime());
    }

    @Test
    public void aircraftReduceFuelTest() {
        Aircraft aircraft = createDummyPlane(200.0f, AircraftStatus.NORMAL, 0.0);
        
        // Normal reduction
        aircraft.reduceFuel(100.0f);
        assertEquals(100.0f, aircraft.getFuel(), 0.001f);
        assertEquals(AircraftStatus.NORMAL, aircraft.getStatus());
        
        // Test threshold: 10 mins/litres should trigger FUEL status
        aircraft.reduceFuel(90.0f);
        assertEquals(10.0f, aircraft.getFuel(), 0.001f);
        assertEquals(AircraftStatus.FUEL, aircraft.getStatus());
        
        // Test floor: Fuel cannot go negative
        aircraft.reduceFuel(20.0f);
        assertEquals(0.0f, aircraft.getFuel(), 0.001f);
    }

    @Test
    public void aircraftSequenceNumberTest() {
        // Verify that the static sequence counter guarantees FIFO ordering
        Aircraft plane1 = createDummyPlane(100.0f, AircraftStatus.NORMAL, 0.0);
        Aircraft plane2 = createDummyPlane(100.0f, AircraftStatus.NORMAL, 0.0);
        
        assertTrue("Plane 2 must have a higher sequence number than Plane 1", 
                    plane2.getSequenceNumber() > plane1.getSequenceNumber());
    }

    @Test
    public void aircraftDelayCalculationTest() {
        // Scheduled at tick 100.0
        Aircraft aircraft = createDummyPlane(100.0f, AircraftStatus.NORMAL, 100.0);
        
        // Delay is 0 before processing
        assertEquals(0.0, aircraft.getDelayTicks(), 0.001);

        // Processed at tick 150.0
        aircraft.setActualTime(150.0);
        
        // Delay should be 50 ticks
        assertEquals(150.0, aircraft.getActualTime(), 0.001);
        assertEquals(50.0, aircraft.getDelayTicks(), 0.001);
    }

    @Test
    public void aircraftStatusPriorityTest() {
        // Essential for Requirement FR 05 (Priority Sorting)
        assertEquals(0, AircraftStatus.NORMAL.getPriority());
        assertEquals(2, AircraftStatus.FUEL.getPriority());
        assertEquals(1, AircraftStatus.MECH_FAILURE.getPriority());
        assertEquals(1, AircraftStatus.PASSENGER_HEALTH.getPriority());
        
        assertEquals("Fuel below 10%.", AircraftStatus.FUEL.getStatus());
    }
}