package com.airport_sim_2.model;
import java.util.List;
import java.util.PriorityQueue;

import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;
import com.airport_sim_2.events.Event;


public class SimulationContext {

    private HoldingPattern holdingPattern;
    private TakeOffQueue takeOffQueue;
    private List<Runway> runways;
    private StatisticsCollector statistics;

    private PriorityQueue<Event> futureEventList;

    public void scheduleEvent(Event event) {
        futureEventList.add(event);
    }

    public Event getNextEvent() {
        return futureEventList.poll();
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

    public StatisticsCollector getStatistics() {
        return statistics;
    }

    public Runway getRunway(int runwayId) {
        
    }
}
