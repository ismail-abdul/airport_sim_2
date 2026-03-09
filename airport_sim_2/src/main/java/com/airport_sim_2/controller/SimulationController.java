package com.airport_sim_2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;


/**
 * SimulationController is responsible for managing the consumption and creation of events in the simulation.
 * This requires access to SimulationContext
*/
public class SimulationController {

    private SimulationContext context;
    private SimulationEngine engine;
    private Random random;
    private double currentTime;
    private double endTime;

    public SimulationController(double endtime) {
        initialiseSimulation();
        this.endTime = endtime;
        random = new Random();
    }

    // Generate an event according to the necessary distribution.
    // Should we be generating a set of events or just one type?
    // Doesn't it make more sense to just generate a new event when the current one is processed. 
    // Emergencies can be generated whatever way we want. Just make it automatic.
    
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
        engine = new SimulationEngine(this.endTime);
    }

    public void startSimulation() {
        /**
        // schedule some aircraft arrivals. generate two airplanes intended to enter holding pattern some time after the beginnnig m
        Aircraft a1 = new Aircraft("BA123", "British Airways", "Paris", "London", 450, 10000, 60, AircraftStatus.NORMAL, null);
        Aircraft a2 = new Aircraft("AF456", "Air France", "Berlin", "London", 430, 9500, 30, AircraftStatus.NORMAL, null);
        
        // Schedule entry into holding pattern
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusMinutes(2);
        context.scheduleEvent(new EnterHP(now, a1));
        context.scheduleEvent(new EnterHP(later, a2));
        
        // run simulation engine.
        while (context.hasMoreEvents()) {
            Event event = context.getNextEvent();
            // advance time. what if we need to delay an event's processing.
            context.setCurrentTime(event.getTime());
            event.process(context);
            
            checkQueues(); // check queue for diversions and cancellations
        }
        */
       engine.run();
        
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
