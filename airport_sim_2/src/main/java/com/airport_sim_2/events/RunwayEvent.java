package com.airport_sim_2.events;

import com.airport_sim_2.model.SimulationContext;

public abstract class RunwayEvent extends AbstractEvent {
    protected final int runwayId;
    protected RunwayEvent(Double eventTime, int runwayId) {
        super(eventTime);  
        this.runwayId = runwayId;
    }

    public int getRunwayId() {
        return runwayId;
    }

    @Override
    public abstract void process(SimulationContext context);
}
