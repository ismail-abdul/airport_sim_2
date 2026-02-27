package com.airport_sim_2.queues;
import com.airport_sim_2.objects.Aircraft;
import java.util.Comparator;

public class AircraftLandingComparator implements Comparator<Aircraft> {
    @Override
    public int compare (Aircraft a1, Aircraft a2) {
        int priorityCompare = Integer.compare(a2.getStatus().getPriority(), a1.getStatus().getPriority());
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        // NOTE - Will implement arrivalTime method after technical team makes decision.
        return a1.getArrivalTime().compareTo(a2.getArrivalTime());
    }
}
