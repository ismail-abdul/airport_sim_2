package com.airport_sim_2.model;

import java.util.PriorityQueue;
import java.util.Random;

import com.airport_sim_2.events.AircraftTakeOff;
import com.airport_sim_2.events.EnterHP;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;

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
    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    
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
        for (int i = 0; i < 2; i++) {
            Aircraft aircraft = genNewAircraft(timestamp);
            EnterHP event = new EnterHP(timestamp, aircraft);
            eventQueue.add(event);
            timestamp += 5*60;
        }

        // for (int j = 0; j < this.getCtx().getRunways().size(); j++) {
        //     Random random = new Random();
        //     double time = random.nextDouble(0, endTime);
        //     Runway r = this.getCtx().getRunways().get(j);
        //     RunwayStatusChangeEvent event = new RunwayStatusChangeEvent(time, r.getId(), RunwayOperationalStatus.SNOW_CLEARANCE);
        //     eventQueue.add(event);
        // }
        
        // Seed the event queue with one initial departure to start the Poisson chain.
        Aircraft firstDeparture = genNewAircraft(currentTime);
        ctx.getTakeOffQueue().enqueue(firstDeparture);
        eventQueue.add(new AircraftTakeOff(firstDeparture, currentTime));

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

            // Pace the simulation and support pause/stop
            try {
                while (paused && !stopped) {
                    Thread.sleep(50);
                }
                if (stopped) break;
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
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

    public double getEndTime() {
        return endTime;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setStop() {
        this.stopped = true;
    }

}
