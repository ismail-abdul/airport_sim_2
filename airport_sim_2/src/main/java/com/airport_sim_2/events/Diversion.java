package com.airport_sim_2.events;
import com.airport_sim_2.objects.Aircraft;

public class Diversion extends AbstractEvent {

    private final Aircraft aircraft;

    public Diversion(double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {
        // NOTE - Techincal team will implement the undefined methods after the next conversation.

        // remove aircraft from holding pattern if still there
        context.getHoldingPattern().remove(aircraft);
        // update statistics
        context.getStatistics().incrementDiversions();
        // mark aircraft state
        context.markAircraftDiverted(aircraft);
    }
}
