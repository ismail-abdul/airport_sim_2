package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;

public class RunwayFreeEvent extends RunwayEvent {

    public RunwayFreeEvent(Double eventTime, int runwayId) {
        super(eventTime, runwayId);
    }

    @Override
    public EventType getType() {
        return EventType.RUNWAY_FREE;
    }

    @Override
    public void process(SimulationContext context) {
        context.getRunway(runwayId);
        // try landing first as it is priority to arrivals
        if (!context.getHoldingPattern().isEmpty()) {
            context.scheduleEvent(new TakeOffEvent(eventTime, runwayId));
            return;
        }
        // try takeoff
        if (!context.getTakeOffQueue().isEmpty()) {
            context.scheduleEvent(new TakeOffEvent(eventTime, runwayId));
        }
    }
    
}
