package com.airport_sim_2.events;

public abstract class RunwayEvent extends AbstractEvent {
    protected final int runwayId;
    protected RunwayEvent(double eventTime, int runwayId) {
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
