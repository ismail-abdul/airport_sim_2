package com.airport_sim_2.queues;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    public int checkCancelled(int runways, long take_off_time, long max_wait_time){
        // Initialise list of cancelled planes and copy the queue
        List<Aircraft> cancelled = new ArrayList<>();
        Queue<Aircraft> copy = new LinkedList<>(queue);

        int position = 1;

        while (!copy.isEmpty()){
            Aircraft aircraft = copy.poll();

            // Calculates the wait times and time the fuel will last 
            double wait_time = Math.ceil((double) position / runways) * take_off_time;

            if (wait_time > max_wait_time){
                cancelled.add(aircraft);
            }
            // ignore plane
            else{
                position++;
            }
        }

        //remove all diverted planes from the holding pattern and return the list of diverted planes
        queue.removeAll(cancelled);
        //return diverted;
        return cancelled.size();
    }

}
