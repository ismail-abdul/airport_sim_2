package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

public class EnterHP extends AbstractEvent {

    private final Aircraft aircraft;

    @Override
    public EventType getType() {
        return EventType.ENTER_HP;
    }
    
    public EnterHP(Double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {
        // add aircraft to holding pattern
        context.getHoldingPattern().enqueue(aircraft);
        // update statistics
        context.getStatistics().updateMaxHoldingSize(context.getHoldingPattern().size());
        // if runway available, schedule landing immediately
        int runwayID = context.findAvailableLandingRunway();
        if (runwayID != -1) {
            context.scheduleEvent(new LeaveHP(eventTime, aircraft));
        }
    }
}
