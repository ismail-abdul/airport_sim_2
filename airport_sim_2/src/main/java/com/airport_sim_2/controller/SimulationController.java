package com.airport_sim_2.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.airport_sim_2.events.EnterHP;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;

public class SimulationController {

    private SimulationContext context;
    // private SimulationEngine engine;

    public SimulationController() {
        initialiseSimulation();
    }

    private void initialiseSimulation() {

        // Create runways
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(1, RunwayOpMode.MIXED_MODE, RunwayOperationalStatus.AVAILABLE));
        runways.add(new Runway(2, RunwayOpMode.LANDING, RunwayOperationalStatus.AVAILABLE));

        // Create queues
        HoldingPattern holdingPattern = new HoldingPattern();
        TakeOffQueue takeOffQueue = new TakeOffQueue();

        // Create statistics
        StatisticsCollector statistics = new StatisticsCollector();

        // Create context
        context = new SimulationContext(holdingPattern, takeOffQueue, runways, statistics);

        // Create engine
        //engine = new SimulationEngine(context);
    }

    public void startSimulation() {

        // schedule some aircraft arrivals
        Aircraft a1 = new Aircraft("BA123", "British Airways", "Paris", "London", 450, 10000, 60, AircraftStatus.NORMAL, null);
        Aircraft a2 = new Aircraft("AF456", "Air France", "Berlin", "London", 430, 9500, 30, AircraftStatus.NORMAL, null);

        // Schedule entry into holding pattern
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusMinutes(2);
        context.scheduleEvent(new EnterHP(now, a1));
        context.scheduleEvent(new EnterHP(later, a2));

        // run simulation
        while (context.hasMoreEvents()) {
            Event event = context.getNextEvent();
            // advance time
            //context.setCurrentTime(event.getEventTime());
            event.process(context);

            checkQueues(); // check queue for diversions and cancellations
        }
    }

    public void checkQueues(){
        int diverted = context.getHoldingPattern().checkDiversions(context.getRunways().size(), context.getLandingDuration(), context.getFuelConsumptionRate());
        // Gets all planes
        // for (Aircraft aircraft : diverted){
        //     context.getStatistics().recordDiversion();
        // }
        if (diverted > 0){
            context.getStatistics().addDiverted(diverted);
        }


        int cancelled = context.getTakeOffQueue().checkCancelled(context.getRunways().size(), context.getTakeOffDuration(), context.getMaxWaitTime());

        if (cancelled > 0){
            context.getStatistics().addDiverted(cancelled);
        }
    }

    public String[] getHoldingPatternCallsigns() {
        return context.getHoldingPattern().getCallsign();
    }

    public String[] getTakeOffQueueCallsigns() {
        return context.getTakeOffQueue().getCallsign();
    }

    public List<Runway> getRunways() {
        return context.getRunways();
    }

    public StatisticsCollector getStatistics() {
        return context.getStatistics();
    }
}
