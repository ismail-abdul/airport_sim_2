package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.PriorityQueue;

/**
 * Unit tests for the Priority Sorting Logic.
 * Proves that emergencies jump the queue, but normal planes maintain FIFO order
 */
public class AircraftLandingComparatorTest {

    private Aircraft createPlane(String callsign, AircraftStatus status) {
        return new Aircraft(callsign, "OP", "ORG", "DST", 200.0f, 100.0f, 1000, status, 0.0);
    }

    @Test
    public void testEmergencyPriorityOverridesArrivalOrder() {
        AircraftLandingComparator comparator = new AircraftLandingComparator();
        PriorityQueue<Aircraft> queue = new PriorityQueue<>(10, comparator);

        //Plane 1 arrives first, but is fine
        Aircraft normalPlane = createPlane("NORM-01", AircraftStatus.NORMAL);
        
        //Plane 2 arrives later, but is running out of fuel
        Aircraft emergencyPlane = createPlane("EMERG-01", AircraftStatus.FUEL);

        queue.add(normalPlane);
        queue.add(emergencyPlane);

        //The Queue should automatically push the emergency plane to the front
        Aircraft firstToLand = queue.poll();
        assertEquals("Emergency plane should jump the queue and land first", 
                     "EMERG-01", firstToLand.getCallsign());
    }

    @Test
    public void testTieBreakerIsFIFO() {
        AircraftLandingComparator comparator = new AircraftLandingComparator();
        PriorityQueue<Aircraft> queue = new PriorityQueue<>(10, comparator);

        //Both planes have the SAME priority (NORMAL)
        //normalPlane1 is created first, so it gets a lower SequenceNumber
        Aircraft normalPlane1 = createPlane("NORM-01", AircraftStatus.NORMAL);
        Aircraft normalPlane2 = createPlane("NORM-02", AircraftStatus.NORMAL);

        //Add them in reverse order just to prove the comparator sorts them properly
        queue.add(normalPlane2);
        queue.add(normalPlane1);

        //normalPlane1 should come out first because its sequence number is older
        Aircraft firstToLand = queue.poll();
        assertEquals("When priority is tied, the plane that arrived first should land first", 
                     "NORM-01", firstToLand.getCallsign());
    }
}