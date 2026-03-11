package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.model.SimulationEngine;

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
    
                double freeTime = currentAircraft.getScheduledTime() + engine.getCtx().getLandingDuration();
    
                if (freeTime < earliestTime) {
                    earliestTime = freeTime;
                }
            }
    
            LeaveHP retry = new LeaveHP(earliestTime, aircraft);
            engine.enqueueEvent(retry);
            return;
        }
    
        // remove aircraft from holding pattern
        engine.getCtx().getHoldingPattern().remove(aircraft);
    
        // occupy runway
        runway.occupy(aircraft);
    
        // Record wait time
        double waitMinutes = (engine.getCurrentTime() - aircraft.getScheduledTime()) / 60.0;
    
        engine.getCtx().getStatistics().recordArrivalWait(waitMinutes);
    
        // Schedule runway release
        RunwayFreeEvent releaseEvent = new RunwayFreeEvent(engine.getCurrentTime() + engine.getCtx().getLandingDuration(), runway.getId());
        engine.enqueueEvent(releaseEvent);
    }
}
