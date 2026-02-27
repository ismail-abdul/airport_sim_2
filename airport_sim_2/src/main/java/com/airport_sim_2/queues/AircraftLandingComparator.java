package com.airport_simulation.queues;
import com.airport_simulation.objects.Aircraft;
import java.util.Comparator;
import com.airport_sim_2.objects.Aircraft;

public class AircraftLandingComparator implements Comparator<Aircraft> {
    @Override
    public int compare (Aircraft a1, Aircraft a2) {
        int priorityCompare = Integer.compare(a2.getStatus().getPriority(), a1.getStatus().getPriority());
        if (priorityCompare != 0) {
            return priorityCompare;
        }

        return Long.compare(a1.getSequenceNumber(), a2.getSequenceNumber());
    }
}



