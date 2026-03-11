package com.airport_sim_2.events;

import java.util.List;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
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
        // Get the relevant runway
        assert !(this.runwayId==-1) : "Invalid runway id for this RunwayFreeEvent";
        List<Runway> list = engine.getCtx().getRunways();
        Runway r = list.get(this.runwayId);
        assert r.isOccupied() 
            : String.format("Runway with id %d isn't occupied. Can't be 'freed'.", runwayId);
        r.release();
        // No need to schedule another instance of the event.
    }
    
}
