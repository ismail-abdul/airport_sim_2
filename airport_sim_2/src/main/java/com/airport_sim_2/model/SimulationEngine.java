package com.airport_sim_2.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import com.airport_sim_2.events.AircraftTakeOff;
import com.airport_sim_2.events.EnterHP;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import com.airport_sim_2.objects.Runway;

/**
 * Manages, creates and processes events 
 * in chronological order.
 * Managing the event Queue as well
 * Uses discrete event simulation with a priority queue.
 */
public class SimulationEngine { 
    private SimulationContext ctx; 
    private PriorityQueue<Event> eventQueue;
    /**
     * The event queue is responsible for scheduling the timing of each event. 
     * Whilst the holding pattern and takeofff queue exist. 
     * The actions of the aircraft need to be scheduled properly. 
     */
    private Map<EventType, EventGenerator> generators;
    private double currentTime;
    private double endTime;
    
    public SimulationEngine(double endTime) {
        this.eventQueue = new PriorityQueue<>();
        this.generators = new HashMap<>();
        for (EventType type: EventType.values()) {
            switch (type) {
                case AC_TAKEOFF:
                    break;
                default:
                    break;
            }
        }
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
    
    /**
     * Generates fresh events for new events.
     */
    public static Aircraft genNewAircraft(Double scheduled_ts) {
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

        // Seed the event queue with landings
        double timestamp = currentTime;
        for (int i = 0; i < 5; i++) {
            Aircraft aircraft = genNewAircraft(timestamp);
            EnterHP event = new EnterHP(currentTime, aircraft);
            ctx.getHoldingPattern().enqueue(aircraft);
            eventQueue.add(event);
            timestamp += 5*60;
        }
        
        // Seed the event queue with takeoffs.
        timestamp = currentTime;
        for (int i = 0; i < 5; i++) {
            // event can be generated annd timestamps during actaul operation of the simulation
            Aircraft aircraft = genNewAircraft(timestamp);
            ctx.getTakeOffQueue().enqueue(aircraft);
            // uniform probability of failure of some kind. Implement failure handling once the basics work.
            AircraftTakeOff event = new AircraftTakeOff(aircraft, currentTime);
            eventQueue.add(event);
            timestamp += 5*60;
        }
        
        
        while (!eventQueue.isEmpty() && currentTime < endTime) {
            // Get the next event (earliest in time) and pop it from the event queue.
            Event event = eventQueue.poll();
            
            // Advance simulation time.
            currentTime = event.getTime();
            
            if (currentTime > endTime) {
                break;
            }
            
            // Process the event 
            event.processEvent(this);
            
            // Generate the next occurrence of this event type
            EventGenerator generator = generators.get(event.getType());

            // Use switch case to decide on timing and necessary objects. 
            Aircraft aircraft = null;
            Runway runway = null;
            Double scheduled_ts = null;
            
            switch (event.getType()) {
                // case RUNWAY_TAKEOFF:
                //     break;
                case ENTER_HP:
                    break;
                // case AIRCRAFT_EM_STATUS_CHANGE:
                //     break;
                // case CRITICAL_FUEL_LEVEL:
                //     break;
                // case DIVERSION:
                //     break;
                case LANDING:
                    break;
                // case LEAVE_HP:
                //     break;
                // case MECHANICAL_FAILURE:
                //     break;
                // case PASSENGER_HEALTH:
                //     break;
                // case RUNWAY_FREE:
                //     break;
                // case RUNWAY_OP_MODE_CHANGE:
                //     break;
                // case RUNWAY_OP_STATUS_CHANGE:
                //     break;
                case TAKEOFF_CANCELLATION:
                    break;
                default:
                    System.out.println("Unimplemented cases. This will likely fail");
                    break;
            }
            
            Event nextEvent = generator.generateNext(currentTime, null, null);
            
            if (nextEvent.getTime() <= endTime) {
                eventQueue.add(nextEvent);
            }
        }
        
        System.out.println("Simulation ended at time: " + currentTime);
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

    public SimulationContext getCtx() {
        return ctx;
    }

    public void enqueueEvent(Event e) {
        eventQueue.add(e);
    }

    public boolean hasMoreEvents() {
        return !eventQueue.isEmpty();
    }

    public Event getNextEvent(){
        return eventQueue.peek();
    }


    /**
     * Removes event from fron of the event queue.
     */
    public void removeEvent() {
        eventQueue.remove();
    }

    public boolean removeEvent(Event e) {
        return eventQueue.remove(e);
    }

}
