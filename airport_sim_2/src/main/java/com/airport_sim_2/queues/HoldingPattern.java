package com.airport_sim_2.queues;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import com.airport_sim_2.objects.Aircraft;

public class HoldingPattern implements AircraftQueue {
    // TEMP CONSTANTS
    double fuel_consumption_rate = 4.0; // fuel (litres) per minutes used
    double landing_times = 20; // minutes needed for a runway to be available after an aircraft lands on it 


    private PriorityQueue<Aircraft> queue;
    private int runways;

    public HoldingPattern(int runways) {
        queue = new PriorityQueue<>(new AircraftLandingComparator());
        this.runways = runways;
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

    public List<Aircraft> checkDiversions(double currentTime) {
        List<Aircraft> diverted = new ArrayList<>();
        PriorityQueue<Aircraft> copy = new PriorityQueue<>(queue);

        int position = 1;

        while (!copy.isEmpty()){
            Aircraft aircraft = copy.poll();
            double wait_time = Math.ceil((double) position / runways) * landing_times;
            double fuel_left = aircraft.getFuel()/fuel_consumption_rate;

            System.out.println("Aircraft: " + aircraft.getCallsign());
            System.out.println("Position: " + position);
            System.out.println("Wait time: " + wait_time);
            System.out.println("Time remaining: " + fuel_left);
            System.out.println();

            if (fuel_left < wait_time){
                diverted.add(aircraft);
            }
            else{
                position++;
            }
        }

        queue.removeAll(diverted);
        return diverted;
    }

    // Gets all the aircrafts callsign in the queue and returns it
    protected String[] getCallsign(){
        // Create a copy of the queue
        PriorityQueue<Aircraft> copy = new PriorityQueue<>(queue);

        int i = 0;
        String[] callsigns = new String[queue.size()];

        // Loop through the copy and get all the aircrafts callsigns
        while(!copy.isEmpty()){
            callsigns[i++] = copy.poll().getCallsign();
        }
        
        return callsigns;
    }
}
