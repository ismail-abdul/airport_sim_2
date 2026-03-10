package com.airport_sim_2.model;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.airport_sim_2.events.Event;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import com.airport_sim_2.objects.Runway;

/**
 * Main simulation engine that manages, creates and processes events 
 * in chronological order.
 * Managing the event Queue as well
 * Uses discrete event simulation with a priority queue.
 */
public class SimulationEngine {
    // I'm questioning why the SimContext needs to be part of the engine.
    // Depends on what you want the engine to be reponsible for.
    // If the sim context is used to generate events and the event queue is kept in engine as well.
    // Then I suppose it makes sense for the engine to alter the queue entirely. 
    // Maybe it just needs to emit events to the view and model as well. 
    // That seems more parellilisable. 
    private SimulationContext ctx; 
    private PriorityQueue<Event> eventQueue;
    private Map<EventType, EventGenerator> generators;
    private double currentTime;
    private double endTime;
    
    public SimulationEngine(double endTime) {
        this.eventQueue = new PriorityQueue<>();
        this.generators = new HashMap<>();
        this.currentTime = 0.0;
        this.endTime = endTime + 60*30;
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
    }
    
    public Aircraft genNewAircraft(Double scheduled_ts) {
        Random random = new Random();
        String callsign = CallsignGenerator.generateCallsign();
        String operator = OperatorGenerator.generateOperator();
        String[] route = RouteGenerator.generateRoute();
        float groundspeed = random.nextFloat(100,400) ; // units of fuel per second
        float fuel = random.nextFloat(groundspeed * 8*60*60); // units of fuel
        Aircraft aircraft = new Aircraft(callsign, operator, route[0], route[1], groundspeed, fuel, 0, AircraftStatus.NORMAL, scheduled_ts);
        return aircraft;
    }
    /**
     * Run the simulation until the end time is reached.
     */
    public void run() {
        System.out.println("Starting simulation...");

        double timestamp = currentTime;
        // Seed the event queue with each necessary type: takeoffs and landings
        for (int i = 0; i < 5; i++) {
            Aircraft aircraft = genNewAircraft(timestamp);
            // EnterHP event = new EnterHP(currentTime, aircraft);
            ctx.getHoldingPattern().enqueue(aircraft);
            // eventQueue.add(event); // 
            timestamp += 5*60;
        }

        timestamp = currentTime;
        for (int i = 0; i < 5; i++) {
            Aircraft aircraft = genNewAircraft(timestamp);
            ctx.getTakeOffQueue().enqueue(aircraft);
            // event can be generated annd timestamps during actaul operation of the simulation
            // eventQueue.add(event)
        }
        
        
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

            // Use switch case to decide on timing and necessary objects. 
            Aircraft aircraft = null;
            Runway runway = null;
            Double scheduled_ts = null;
            
            switch (event.getType()) {
                case TAKEOFF:
                    break;
                case ENTER_HP:
                    break;
                case AIRCRAFT_EM_STATUS_CHANGE:
                    break;
                case CRITICAL_FUEL_LEVEL:
                    break;
                case DIVERSION:
                    break;
                case LANDING:
                    break;
                case LEAVE_HP:
                    break;
                case MECHANICAL_FAILURE:
                    break;
                case PASSENGER_HEALTH:
                    break;
                case RUNWAY_FREE:
                    break;
                case RUNWAY_OP_MODE_CHANGE:
                    break;
                case RUNWAY_OP_STATUS_CHANGE:
                    break;
                case TAKEOFF_CANCELLATION:
                    break;
                default:
                    System.out.println("Unimplemented cases. This will likely fail");
                    break;
            }
            
            Event nextEvent = generator.generateNext(currentTime, null, null);
            
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
