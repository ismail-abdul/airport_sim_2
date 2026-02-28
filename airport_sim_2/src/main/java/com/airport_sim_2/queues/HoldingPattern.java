package com.airport_sim_2.queues;
import java.util.PriorityQueue;
import com.airport_sim_2.objects.Aircraft;

public class HoldingPattern implements AircraftQueue {

    private final PriorityQueue<Aircraft> queue;

    public HoldingPattern(int runways) {
        queue = new PriorityQueue<>(new AircraftLandingComparator());
    }

    @Override
    public void enqueue(Aircraft aircraft) {
        queue.add(aircraft);
    }

    @Override
    public Aircraft dequeue() {
        return queue.poll();
    }

    @Override
    public int size() {
        return queue.size();
    }

    public boolean remove(Aircraft aircraft) {
        return queue.remove(aircraft);
    }

    public boolean contains(Aircraft aircraft) {
        return queue.contains(aircraft);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
