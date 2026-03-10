package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

/**
 * Unit tests for the Holding Pattern.
 * Validates the priority-based sorting and the diversion logic .
 */
public class HoldingPatternTest {

    private Aircraft createPlane(String callsign, float fuel) {
        //Using AircraftStatus.NORMAL to test how fuel alone triggers the logic
        return new Aircraft(callsign, "OP", "ORG", "DST", 200f, 1000f, fuel, AircraftStatus.NORMAL, LocalDateTime.now());
    }

    @Test
    public void testHoldingPatternSorting() {
        HoldingPattern hp = new HoldingPattern();
        
        //Plane 1 arrives first but is Normal
        hp.enqueue(createPlane("NORM", 100f));
        //Plane 2 arrives second but has low fuel (FUEL status trigger)
        Aircraft emergency = createPlane("EMG", 5f);
        emergency.reduceFuel(0f); //Triggers the internal status update to FUEL
        hp.enqueue(emergency);

        //The first callsign returned should be the Emergency one due to priority
        String[] calls = hp.getCallsign();
        assertEquals("EMG", calls[0]);
        assertEquals("NORM", calls[1]);
    }

    @Test
    public void testDiversionLogic() {
        HoldingPattern hp = new HoldingPattern();
        
        //Setup: 1 runway, 5 mins per landing
        //Plane A: 20 mins fuel. Wait time = 5 mins. (Should stay)
        hp.enqueue(createPlane("STAY", 20f));
        //Plane B: 4 mins fuel. Wait time = 10 mins. (Should divert)
        hp.enqueue(createPlane("DIVERT", 4f));

        //Params: 1 runway, 5 min landing, 1 fuel rate
        int result = hp.checkDiversions(1, 5, 1);

        assertEquals("One aircraft should have diverted", 1, result);
        assertEquals(1, hp.size());
        assertEquals("STAY", hp.dequeue().getCallsign());
    }
}
