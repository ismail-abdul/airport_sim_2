package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

// This events is dispatched to the UI
public class LeaveHP extends AbstractEvent {
    private final Aircraft aircraft;

    @Override
    public EventType getType() {
        return EventType.LEAVE_HP;
    }
    
    public LeaveHP(Double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Event event) {
        return this.getTime().compareTo(event.getTime());
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    @Override 
    public void processEvent(SimulationEngine engine) {
        // check if aircraft exists
        if (!engine.getCtx().getHoldingPattern().contains(aircraft)) {
            return;
        }

        Runway runway = engine.getCtx().getLandingRunway();
        if (runway == null) {
            // No runway available → reschedule when earliest runway frees
            double earliestTime = Double.MAX_VALUE;
    
            for (Runway r : engine.getCtx().getRunways()) {
                Aircraft currentAircraft = r.getCurrentAircraft();
                if (currentAircraft == null) {
                    continue;
                }  
    
                // changed from scheduled aircraft time to current engine time
                double freeTime = engine.getCurrentTime() + engine.getCtx().getLandingDuration();
    
                if (freeTime < earliestTime) {
                    earliestTime = freeTime;
                }
            }
    
            if (earliestTime <= engine.getEndTime()) {
                LeaveHP retry = new LeaveHP(earliestTime, aircraft);
                engine.enqueueEvent(retry);
            }
            return;
        }
    
        // remove aircraft from holding pattern
        engine.getCtx().getHoldingPattern().remove(aircraft);
    
        // occupy runway
        runway.occupy(aircraft);
    
        // Record wait time and arrival
        double waitMinutes = (engine.getCurrentTime() - aircraft.getScheduledTime()) / 60.0;
        engine.getCtx().getStatistics().recordArrivalWait(waitMinutes);
        engine.getCtx().getStatistics().incrementArrived();
        engine.getCtx().getStatistics().arrival_ts_add(
            new com.airport_sim_2.controller.TimeSeriesPoint(
                engine.getCurrentTime(), engine.getCtx().getStatistics().getArrivedCount()
            )
        );
    
        // Schedule runway release
        RunwayFreeEvent releaseEvent = new RunwayFreeEvent(engine.getCurrentTime() + engine.getCtx().getLandingDuration(), runway.getId());
        engine.enqueueEvent(releaseEvent);
    }
}
