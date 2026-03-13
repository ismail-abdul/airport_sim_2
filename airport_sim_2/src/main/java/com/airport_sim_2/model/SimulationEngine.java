package com.airport_sim_2.model;

import java.util.PriorityQueue;
import java.util.Random;

import com.airport_sim_2.events.AircraftTakeOff;
import com.airport_sim_2.events.EnterHP;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.events.RunwayStatusChangeEvent;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOperationalStatus;

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
    private double currentTime;
    private double endTime;
    
    public SimulationEngine(double endTime, SimulationContext ctx) {
        this.eventQueue = new PriorityQueue<>();
        this.currentTime = 0.0;
        this.endTime = endTime;
        this.ctx = ctx;
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
            EnterHP event = new EnterHP(timestamp, aircraft);
            ctx.getHoldingPattern().enqueue(aircraft);
            eventQueue.add(event);
            timestamp += 5*60;
        }

        for (int j = 0; j < this.getCtx().getRunways().size(); j++) {
            Random random = new Random();
            double time = random.nextDouble(0, endTime);
            Runway r = this.getCtx().getRunways().get(j);
            RunwayStatusChangeEvent event = new RunwayStatusChangeEvent(time, r.getId(), RunwayOperationalStatus.SNOW_CLEARANCE);
            eventQueue.add(event);
        }
        
        // Seed the event queue with takeoffs.
        timestamp = currentTime;
        // for (int i = 0; i < 5; i++) {
        //     // event can be generated annd timestamps during actaul operation of the simulation
        //     Aircraft aircraft = genNewAircraft(timestamp);
        //     ctx.getTakeOffQueue().enqueue(aircraft);
        //     // uniform probability of failure of some kind. Implement failure handling once the basics work.
        //     AircraftTakeOff event = new AircraftTakeOff(aircraft, timestamp);
        //     eventQueue.add(event);
        //     timestamp += 5*60;
        // }

        for (int i = 0; i < 5; i++) {

        }
        
        
        while (!eventQueue.isEmpty() && currentTime < endTime) {
            System.out.println("Event queue before processing");
            printEventQueue();
            // Get the next event (earliest in time) and pop it from the event queue.
            Event event = eventQueue.poll();
            
            // Advance simulation time.
            currentTime = event.getTime();
            System.out.println("Processing event: " + event.getType() + " at time " + currentTime);

            if (event.getTime() < currentTime) {
                System.out.println("WARNING: event scheduled in the past or same time");
            }
            if (currentTime > endTime) {
                break;
            }
            // Process the event 
            event.processEvent(this);
            System.out.println("Event queue before processing");
            printEventQueue();
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

    public void printEventQueue() {
        for (Event e : eventQueue) {
            System.out.println(e.getType() + "@" + e.getTime());
        }
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
