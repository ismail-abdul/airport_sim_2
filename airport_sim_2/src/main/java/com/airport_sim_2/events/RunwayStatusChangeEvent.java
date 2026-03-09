package com.airport_sim_2.events;


import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.RunwayOperationalStatus;

public class RunwayStatusChangeEvent extends RunwayEvent {

    @Override
    public EventType getType() {
        return EventType.RUNWAY_OP_STATUS_CHANGE;
    }

    private final RunwayOperationalStatus newStatus;
    public RunwayStatusChangeEvent(Double eventTime, int runwayId, RunwayOperationalStatus newStatus) {
        super(eventTime, runwayId);
        this.newStatus = newStatus;
    }
    @Override
    public void process(SimulationContext context) {
        context.getRunway(runwayId).setStatus(newStatus);
    }
}
