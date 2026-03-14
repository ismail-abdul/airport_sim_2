package com.airport_sim_2.objects;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Runway, Operational Modes, and Statuses.
 */
public class RunwayTest {

    private Aircraft createDummyPlane() {
        return new Aircraft("TEST", "OP", "ORG", "DST", 200.0f, 100.0f, 1000, AircraftStatus.NORMAL, 0.0);
    }

    @Test
    public void testRunwayInitialization() {
        Runway runway = new Runway(1, RunwayOpMode.LANDING, RunwayOperationalStatus.AVAILABLE);
        
        assertEquals(1, runway.getId());
        assertEquals(RunwayOpMode.LANDING, runway.getMode());
        assertEquals(RunwayOperationalStatus.AVAILABLE, runway.getStatus());
        assertFalse("Runway should be empty initially", runway.isOccupied());
        assertEquals(0, runway.getOperationCount());
    }

    @Test
    public void testRunwayOccupancyAndAnalytics() {
        Runway runway = new Runway(1, RunwayOpMode.LANDING, RunwayOperationalStatus.AVAILABLE);
        Aircraft plane = createDummyPlane();

        // Occupy runway
        runway.occupy(plane);
        assertTrue("Runway should be occupied", runway.isOccupied());
        assertEquals(plane, runway.getCurrentAircraft());
        assertEquals("Operation count should increment", 1, runway.getOperationCount());

        // Release runway
        runway.release();
        assertFalse("Runway should be empty after release", runway.isOccupied());
        assertNull(runway.getCurrentAircraft());
        
        // Operation count shouldn't decrease on release
        assertEquals(1, runway.getOperationCount());
    }

    @Test
    public void testLandingAvailability() {
        Runway runway = new Runway(1, RunwayOpMode.LANDING, RunwayOperationalStatus.AVAILABLE);
        
        // Base case: Available and Landing mode
        assertTrue(runway.isAvailableForLanding());
        assertFalse(runway.isAvailableForTakeoff());

        // Mixed Mode should allow landing
        runway.setMode(RunwayOpMode.MIXED_MODE);
        assertTrue(runway.isAvailableForLanding());

        // Occupied runway cannot accept landings
        runway.occupy(createDummyPlane());
        assertFalse("Occupied runway cannot accept landing", runway.isAvailableForLanding());
        runway.release();

        // Closed/Inspection runway cannot accept landings
        runway.setStatus(RunwayOperationalStatus.INSPECTION);
        assertFalse("Runway under inspection cannot accept landing", runway.isAvailableForLanding());
    }

    @Test
    public void testTakeoffAvailability() {
        Runway runway = new Runway(1, RunwayOpMode.TAKE_OFF, RunwayOperationalStatus.AVAILABLE);
        
        //Base case: Available and Takeoff mode
        assertTrue(runway.isAvailableForTakeoff());
        assertFalse(runway.isAvailableForLanding());

        //Mixed Mode should allow takeoff
        runway.setMode(RunwayOpMode.MIXED_MODE);
        assertTrue(runway.isAvailableForTakeoff());
    }
}