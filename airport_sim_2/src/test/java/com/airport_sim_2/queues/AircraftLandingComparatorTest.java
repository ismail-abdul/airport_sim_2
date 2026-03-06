package com.airport_sim_2.queues;

import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;


public class AircraftLandingComparatorTest {

    @Test
    public void testEmergencyPriorityOverNormal() {
        AircraftLandingComparator comparator = new AircraftLandingComparator();
        LocalDateTime now = LocalDateTime.now();

        //Create a normal plane first (lower sequence number)
        Aircraft normalPlane = new Aircraft("NML1", "OP", "ORG", "DST", 200f, 1000f, 100f, AircraftStatus.NORMAL, now);
        //Create an emergency plane second (higher sequence number)
        Aircraft emergencyPlane = new Aircraft("EMG1", "OP", "ORG", "DST", 200f, 1000f, 5f, AircraftStatus.FUEL, now);

        //Checking that even though normalPlane arrived first, emergencyPlane MUST be sorted ahead of it.
        //A negative result from compare(a, b) means 'a' comes before 'b'.
        int result = comparator.compare(emergencyPlane, normalPlane);
        assertTrue("Emergency plane must have priority over normal plane", result < 0);
    }

    @Test
    public void testFIFOOrderingForSamePriority() {
        AircraftLandingComparator comparator = new AircraftLandingComparator();
        LocalDateTime now = LocalDateTime.now();

        //Create two normal planes
        Aircraft plane1 = new Aircraft("NML1", "OP", "ORG", "DST", 200f, 1000f, 100f, AircraftStatus.NORMAL, now);
        Aircraft plane2 = new Aircraft("NML2", "OP", "ORG", "DST", 200f, 1000f, 100f, AircraftStatus.NORMAL, now);

        //Since priorities are equal, it must fall back to FIFO (sequence number).
        //plane1 arrived first, so it should be sorted ahead of plane2.
        int result = comparator.compare(plane1, plane2);
        assertTrue("Planes with same priority must be sorted by FIFO (Sequence Number)", result < 0);
    }
}
