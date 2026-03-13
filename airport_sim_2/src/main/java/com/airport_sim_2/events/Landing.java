package com.airport_sim_2.events;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.controller.TimeSeriesPoint;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;


/**
 * The act of an aircraft literally landing on a runway.
 * If Landing can happen, then a runway occupation should occur after the fact. 
 * 
 */
public class Landing extends AbstractEvent   {
    private final Aircraft aircraft;
    private final int runwayID;
    
    public Landing(Double eventTime, Aircraft aircraft, int runwayID) {
        super(eventTime);
        this.aircraft = aircraft;
        this.runwayID = runwayID;
    }

    @Override
    public EventType getType() {
        return EventType.LANDING;
    }

    /* Processes a landing an event, updates the context and engine.
        Uses greedy approach. 

        1. Consider if there is an available runway
        If there is, land the plane.
        One should also consider if there are multiple runways available.
        What if we can schedule many landing simultaneously?
        Solution: fill as many runways as possible immediately
        Then advance by ticks. Start the landing process.
        At what point during the landing can we lock in 

        2. Greedily schedule events. If you can't schedule something just wait.
        To deal with multiple runways, just schedule the acts of a runways being occupied/freed when you intiate a landing.
        Premptive scheduling of events. And you make the aircraft wait until 
        the next runway is freed up. 
        
        Just make sure they're strictly in order of time processing.
    */
    @Override
    public void process(SimulationContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Handle landing with greed.
    @Override
    public void processEvent(SimulationEngine engine) {
        // If wait time is exceeded, schedule a diversion
        if (this.eventTime - aircraft.getScheduledTime() >= engine.getCtx().getMaxWaitTime()) {
            Diversion event = new Diversion(this.eventTime, this.aircraft);
            engine.enqueueEvent(event);
            // record diversion in timeseries
            StatisticsCollector stats = engine.getCtx().getStatistics();
            int div_count = stats.getDivertedCount();
            TimeSeriesPoint p = new TimeSeriesPoint(eventTime, div_count+1);
            stats.incrementDiverted();
            return;
        }

        Runway runway = engine.getCtx().getLandingRunway();
        if (runway != null) {
            // occupy runway, scheduled it's freeing, more forward time
            assert runway.isAvailableForLanding();
            this.aircraft.setActualTime(engine.getCurrentTime());
            runway.occupy(this.aircraft);
            RunwayFreeEvent event = new RunwayFreeEvent(
                this.eventTime + engine.getCtx().getLandingDuration(),
                runway.getId()  
            );
            engine.enqueueEvent(event);

        } else {
            // find the earliest time any runway becomes free
            double earliestTime = Double.MAX_VALUE;
            int runway_id = -1;
            for (Runway r : engine.getCtx().getRunways()) {
                Aircraft currentAircraft = r.getCurrentAircraft();
                if (currentAircraft == null) 
                    continue;
                double freeTime =
                    currentAircraft.getScheduledTime() +
                    engine.getCtx().getLandingDuration();

                if (freeTime < earliestTime) {
                    earliestTime = freeTime;
                    runway_id = r.getId();
                }
            }
    
            // pre-emptively reschedule the landing event
            Landing retry = new Landing(earliestTime, this.aircraft, runway_id);
            engine.enqueueEvent(retry);
        }

    }


}
