package com.airport_sim_2.model;
import java.util.List;
import java.util.PriorityQueue;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;

/**
 * SimulationContext is responsible for holding the state of the simulation and holding metadata.
 * That includes an event queue, holding pattern, takeoff queue, runways. 
 * 
 * Instead, maybe it should contain all the configuartion information about the simulation.
 * e.g. before starting the simulation, all this information should be collected.
 */
public class SimulationContext {

    private HoldingPattern holdingPattern;
    private TakeOffQueue takeOffQueue;
    private List<Runway> runways;
    private StatisticsCollector statistics;
    private PriorityQueue<Event> futureEventList;
    // minutes
    private final long landingDuration = 20; 
    private final long takeOffDuration = 15;
    //fuel used per minute
    private final long fuel_consumption_rate = 20;
    // max wait time for departing aircraft
    private final long max_wait_time = 30;
    private double current_time = 0;

    public PriorityQueue<Event> getFutureEventList() {
        return futureEventList;
    }

    public SimulationContext(HoldingPattern holdingPattern, TakeOffQueue takeOffQueue, List<Runway> runways, StatisticsCollector statistics) {
        this.holdingPattern = holdingPattern;
        this.takeOffQueue = takeOffQueue;
        this.runways = runways;
        this.statistics = statistics;
        this.futureEventList = new PriorityQueue<>();
    }

    public void scheduleEvent(Event event) {
        futureEventList.add(event);
    }

    public Event getNextEvent() {
        return futureEventList.poll();
    }

    public boolean hasMoreEvents() {
        return !futureEventList.isEmpty();
    }

    public HoldingPattern getHoldingPattern() {
        return holdingPattern;
    }

    public TakeOffQueue getTakeOffQueue() {
        return takeOffQueue;
    }

    public List<Runway> getRunways() {
        return runways;
    }
    
    public double getCurrentTime() {
        return current_time;
    }

    public void setCurrentTime(double newtime) {
        current_time = newtime;
    }

    // get's any available runway or returns null
    public Runway getAvailableRunway() {
        for (int i = 0; i < runways.size(); i++) {
            try {
                Runway runway = runways.get(i);
                return runway;
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException("Index exceeds bounds of runway count");
            }
        }
        return null;
    }
    
    public Runway getRunway(int runwayId) {
        return runways.stream().filter(r -> r.getId() == runwayId).findFirst().orElse(null);
    }

    public int findAvailableLandingRunway() {
        for (Runway runway : runways) {
            if (runway.isAvailableForLanding()) {
                return runway.getId();
            }
        }
        return -1;
    }

    public long getLandingDuration() {
        return landingDuration;
    }

    public long getTakeOffDuration() {
        return takeOffDuration;
    }

    public long getMaxWaitTime() {
        return max_wait_time;
    }

    public long getFuelConsumptionRate(){
        return fuel_consumption_rate;
    }

    public StatisticsCollector getStatistics() {
        return statistics;
    }

    public boolean isLandingRunwayAvailable() {
        return runways.stream().anyMatch(Runway::isAvailableForLanding);
    }

    public void markAircraftDiverted(Aircraft aircraft) {
        System.out.println("Aircraft diverted");
    }

    public void tryScheduleLanding(int runwayId) {
        
    }
}
