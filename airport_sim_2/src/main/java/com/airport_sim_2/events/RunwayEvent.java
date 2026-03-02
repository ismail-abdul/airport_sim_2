package com.airport_sim_2.events;

import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;

public abstract class RunwayEvent extends AbstractEvent {
    protected final int runwayId;
    protected RunwayEvent(LocalDateTime eventTime, int runwayId) {
        super(eventTime);  
        this.runwayId = runwayId;
    }

    public int getRunwayId() {
        return runwayId;
    }

    @Override
    public void process(SimulationContext context) {
        
    }
}
