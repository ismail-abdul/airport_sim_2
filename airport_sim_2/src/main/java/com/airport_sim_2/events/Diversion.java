package com.airport_sim_2.events;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

public class Diversion extends AbstractEvent {

    private final Aircraft aircraft;

    public Diversion(double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {

        // remove aircraft from holding pattern if still there
        context.getHoldingPattern().remove(aircraft);
        // update statistics
        context.getStatistics().incrementDiverted();
        // mark aircraft state
        context.markAircraftDiverted(aircraft);
    }
}
