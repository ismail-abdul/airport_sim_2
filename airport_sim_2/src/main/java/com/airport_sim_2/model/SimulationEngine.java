package com.airport_sim_2.model;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;

import com.airport_sim_2.events.Event;

/**
 * Main simulation engine that manages, creates and processes events 
 * in chronological order.
 * Managing the event Queue as well
 * Uses discrete event simulation with a priority queue.
 */
public class SimulationEngine {
    private PriorityQueue<Event> eventQueue;
    private Map<EventType, EventGenerator> generators;
    private double currentTime;
    private double endTime;
    
    public SimulationEngine(double endTime) {
        this.eventQueue = new PriorityQueue<>();
        this.generators = new HashMap<>();
        this.currentTime = 0.0;
        this.endTime = endTime;
    }
    
    /**
     * Register an event generator for a specific event type.
     * 
     * @param eventType The type of event
     * @param mean Mean interval between events
     * @param stdDev Standard deviation of interval
     */
    public void registerEventType(EventType eventType, double mean, double stdDev) {
        EventGenerator generator = new EventGenerator(eventType, mean, stdDev);
        generators.put(eventType, generator);
        
        // Generate the first event of this type
        // Event initialEvent = generator.generateNext(0.0);
        // eventQueue.add(initialEvent);
    }
    
    /**
     * Run the simulation until the end time is reached.
     */
    public void run() {
        System.out.println("Starting simulation...");
        
        while (!eventQueue.isEmpty() && currentTime < endTime) {
            // Get the next event (earliest in time)
            Event event = eventQueue.poll();
            
            // Advance simulation time
            currentTime = event.getTime();
            
            if (currentTime > endTime) {
                break;
            }
            
            // Process the event
            processEvent(event);
            
            // Generate the next occurrence of this event type
            EventGenerator generator = generators.get(event.getType());
            Event nextEvent = generator.generateNext(currentTime);
            
            if (nextEvent.getTimestamp() <= endTime) {
                eventQueue.add(nextEvent);
            }
        }
        
        System.out.println("Simulation ended at time: " + currentTime);
    }
    
    /**
     * Process a single event. This is where you implement your simulation logic.
     * 
     * TODO: Implement the specific behavior for each event type
     */
    private void processEvent(Event event) {
        System.out.println("Processing: " + event);
        
        // TODO: Implement event-specific logic here
        // Use instanceOf instead
        switch (event.getType()) {
            default:
                System.out.println("Unknown event type: " + event.getType());
        }
    }
    
    /**
     * Get the current simulation time.
     */
    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }
}
