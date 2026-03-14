package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Holding Pattern.
 * Validates priority-based sorting and diversion logic.
 */
public class HoldingPatternTest {

    private Aircraft createPlane(String callsign, float fuel, AircraftStatus status) {
        return new Aircraft(callsign, "OP", "ORG", "DST", 200.0f, fuel, 10000, status, 0.0);
    }

    @Test
    public void testHoldingPatternSorting() {
        HoldingPattern hp = new HoldingPattern();
        
        //Plane 1 arrives first but is Normal
        hp.enqueue(createPlane("NORM", 100.0f, AircraftStatus.NORMAL));
        
        //Plane 2 arrives second but has an Emergency Status
        hp.enqueue(createPlane("EMERGENCY", 5.0f, AircraftStatus.FUEL));

        //The first callsign returned should be the Emergency one due to the PriorityQueue
        String[] calls = hp.getCallsign();
        assertEquals("EMERGENCY should jump to the front of the line", "EMERGENCY", calls[0]);
        assertEquals("NORM", calls[1]);
    }

    @Test
    public void testDiversionLogic() {
        HoldingPattern hp = new HoldingPattern();
        
        //Setup: 1 runway, 5 mins per landing
        //Plane A: 20 mins fuel. Wait time = 5 mins. (Should stay)
        hp.enqueue(createPlane("STAY", 20.0f, AircraftStatus.NORMAL));
        //Plane B: 4 mins fuel. Wait time = 10 mins. (Should divert)
        hp.enqueue(createPlane("DIVERT", 4.0f, AircraftStatus.NORMAL));

        //Params: 1 runway, 5 min landing time, 1 fuel consumption rate
        int divertedCount = hp.checkDiversions(1, 5, 1);

        assertEquals("One aircraft should have diverted", 1, divertedCount);
        assertEquals(1, hp.size());
        assertEquals("STAY", hp.dequeue().getCallsign());
    }
}