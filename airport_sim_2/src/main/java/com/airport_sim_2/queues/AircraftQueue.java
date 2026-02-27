package com.airport_simulation.queues;
import com.airport_simulation.objects.Aircraft;

public interface AircraftQueue {
    void enqueue(Aircraft aircraft);
    Aircraft dequeue();
    int size();
}
