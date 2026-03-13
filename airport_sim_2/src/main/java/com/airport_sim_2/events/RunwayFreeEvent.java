package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

/**
 * Describes the action of unoccupying a specific runway.
 * Whether that be for a landing or take-off.
 */
public class RunwayFreeEvent extends RunwayEvent {

    public RunwayFreeEvent(Double eventTime, int runwayId) {
        super(eventTime, runwayId);
    }

    @Override
    public EventType getType() {
        return EventType.RUNWAY_FREE;
    }

    /**
     * TODO: make it correct. AI clearly lacked the context to actually make this method work.
    */
    @Override
    public void process(SimulationContext context) {
    /** 
        System.out.println("RunwayFreeEvent can't be processed with this function. It is broken. use processEvent(SimulationEngine engine) instead");
        context.getRunway(runwayId);
        // try landing first as it is priority to arrivals
        if (!context.getHoldingPattern().isEmpty()) {
            context.scheduleEvent(new RunwayTakeOff(eventTime, runwayId));
            return;
        }
        // try takeoff
        if (!context.getTakeOffQueue().isEmpty()) {
            context.scheduleEvent(new RunwayTakeOff(eventTime, runwayId));
        }
    */
    }

    /**
     * Processed a runway freed event. 
     * This just unmarks the runway as unocccupied in the runway list.
     * May throw exception if runway doesn't exist, or is already free.
     */
    @Override
    public void processEvent(SimulationEngine engine) {
        Runway runway = engine.getCtx().getRunway(runwayId);
        if (runway == null) {
            throw new IllegalStateException("Runway " + runwayId + " does not exist.");
        }

        // Free the runway
        runway.release();   

        // Landing has priority over takeoff
        if (!engine.getCtx().getHoldingPattern().isEmpty()) {
            Aircraft aircraft = engine.getCtx().getHoldingPattern().peek();
            engine.enqueueEvent(new LeaveHP(engine.getCurrentTime(), aircraft));
            return;
        }

        // If no landing aircraft, try takeoff
        if (!engine.getCtx().getTakeOffQueue().isEmpty()) {
            Aircraft aircraft = engine.getCtx().getTakeOffQueue().peek();
            engine.enqueueEvent(new AircraftTakeOff(aircraft, engine.getCurrentTime()));
        }
    }
}
