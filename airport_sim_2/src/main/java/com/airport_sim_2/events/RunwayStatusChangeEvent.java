package com.airport_sim_2.events;

import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.RunwayOperationalStatus;

public class RunwayStatusChangeEvent extends RunwayEvent {

    private final RunwayOperationalStatus newStatus;
    public RunwayStatusChangeEvent(LocalDateTime eventTime, int runwayId, RunwayOperationalStatus newStatus) {
        super(eventTime, runwayId);
        this.newStatus = newStatus;
    }
    @Override
    public void process(SimulationContext context) {
        context.getRunway(runwayId).setStatus(newStatus);
    }
}
