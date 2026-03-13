package com.airport_sim_2.queues;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.airport_sim_2.objects.Aircraft;

public class HoldingPattern implements AircraftQueue {

    private final PriorityQueue<Aircraft> queue;

    public HoldingPattern() {
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

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Checks for all planes that need to be diverted from the holding pattern
    public int checkDiversions(int runways, long landing_times, long fuel_consumption_rate) {
        // Initialise list of diverted planes and copy the queue
        List<Aircraft> diverted = new ArrayList<>();
        PriorityQueue<Aircraft> copy = new PriorityQueue<>(queue);

        int position = 1;

        while (!copy.isEmpty()){
            Aircraft aircraft = copy.poll();

            // Calculates the wait times and time the fuel will last 
            double wait_time = Math.ceil((double) position / runways) * landing_times;
            double fuel_left = aircraft.getFuel()/fuel_consumption_rate;

            //plane should be diverted
            if (fuel_left < wait_time){
                diverted.add(aircraft);
            }
            // ignore plane
            else{
                position++;
            }
        }

        //remove all diverted planes from the holding pattern and return the list of diverted planes
        queue.removeAll(diverted);
        //return diverted;
        return diverted.size();
    }
    
    // Gets all the aircrafts callsign in the queue and returns it
    @Override
    public String[] getCallsign(){
        // Create a copy of the queue
        PriorityQueue<Aircraft> copy = new PriorityQueue<>(queue);

        int index = 0;
        String[] callsigns = new String[queue.size()];

        // Loop through the copy and get all the aircrafts callsigns
        while(!copy.isEmpty()){
            callsigns[index++] = copy.poll().getCallsign();
        }
        
        return callsigns;
    }
 
    public Aircraft peek() {
        return queue.peek();
    }

    public PriorityQueue<Aircraft> getQueue() {
        return queue;
    }
}
