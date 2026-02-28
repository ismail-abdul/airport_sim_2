package com.airport_sim_2.model;
import java.util.List;
import java.util.PriorityQueue;

import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;
import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.events.Event;


public class SimulationContext {

    private HoldingPattern holdingPattern;
    private TakeOffQueue takeOffQueue;
    private List<Runway> runways;
    private StatisticsCollector statistics;
    private PriorityQueue<Event> futureEventList;

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

    public Runway getRunway(int runwayId) {
        return runways.stream().filter(r -> r.getId() == runwayId).findFirst().orElse(null);
    }

    public StatisticsCollector getStatistics() {
        return statistics;
    }

    public boolean isLandingRunwayAvailable() {
        return runways.stream().anyMatch(Runway::isAvailableForLanding);
    }
}
