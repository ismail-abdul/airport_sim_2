package com.airport_sim_2.queues;
import java.util.LinkedList;
import java.util.Queue;

import com.airport_sim_2.objects.Aircraft;


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

    @Override 
    public boolean isEmpty() {
        return (queue.size() == 0);
    }

    // Gets all the aircrafts callsign in the queue and returns it
    @Override
    public String[] getCallsign(){

        String[] callsigns = new String[queue.size()];
        int index = 0;

        // Loop through the copy and get all the aircrafts callsigns
        for (Aircraft aircraft : queue){
            callsigns[index++] = aircraft.getCallsign();
        }
        
        return callsigns;
    }
}
