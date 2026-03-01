package com.airport_sim_2.events;

public class RunwayStatusChangeEvent extends RunwayEvent {

    private final RunwayOperationalStatus newStatus;
    public RunwayStatusChangeEvent(double eventTime, int runwayId, RunwayOperationalStatus newStatus) {
        super(eventTime, runwayId);
        this.newStatus = newStatus;
    }
    @Override
    public void process(SimulationContext context) {
        context.getRunway(runwayId).setStatus(newStatus);
    }
}
