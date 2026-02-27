package com.airport_sim_2.queues;
import com.airport_sim_2.objects.Aircraft;

public interface AircraftQueue {
    void enqueue(Aircraft aircraft);
    Aircraft dequeue();
    int size();
}
