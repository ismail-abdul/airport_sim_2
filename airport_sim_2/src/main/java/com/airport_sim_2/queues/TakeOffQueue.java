package com.airport_simulation.queues;
import java.util.LinkedList;
import java.util.Queue;

import com.airport_simulation.objects.Aircraft;


public class TakeOffQueue implements AircraftQueue {
    private Queue<Aircraft> queue = new LinkedList<>();

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
}
