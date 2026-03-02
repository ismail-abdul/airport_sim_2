package com.airport_sim_2.controller;

import java.util.PriorityQueue;

import com.airport_sim_2.events.Event;
import com.airport_sim_2.model.SimulationContext;

public class SimulationController {
    SimulationContext context;

    public SimulationController(SimulationContext context) {
        this.context = context;
    }

    public Event genEvent(Simulation context) {
        
    }

    /*
    Creates a specified number of events. 
    Adds them to the event queue.
     */
    public void createEvents(int num) {
        PriorityQueue<Event> event_q = this.context.getFutureEventList();
        for (int i = 0; i < num; i++) {
            // Add an event 
        }
        // Randomised type, time
        // Sequential. Events need to happen after now. Default of 10 minutes between events.
        // holidng pattern
        // take off queue
        // consider runways

    }

    public void startProcessing() {
        while (!this.context.getFutureEventList().isEmpty()) {
            // Process the next event
        }
    }
}
