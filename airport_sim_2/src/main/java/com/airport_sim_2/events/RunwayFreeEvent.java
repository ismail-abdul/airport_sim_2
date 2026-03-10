package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;

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

    @Override
    /**
     * TODO: make it correct. AI clearly lacked the context to actually make this method work.
     */
    public void process(SimulationContext context) {
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
    }

    /**
     * Processed a runway freed event. 
     * This just unmarks the runway as unocccupied in the runway list.
     * 
     * May throw exception if runway doesn't exist, or is already free.
     */
    public void processEvent(SimulationEngine engine) {
        
    }
    
}
