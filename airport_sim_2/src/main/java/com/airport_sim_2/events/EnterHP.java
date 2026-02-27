package com.airport_sim_2.events;
import com.airport_sim_2.objects.Aircraft;

public class EnterHP extends AbstractEvent {

    private final Aircraft aircraft;
    public EnterHP(double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {
        // Add aircraft to holding pattern
        context.getHoldingPattern().enqueue(aircraft);
        // Update statistics
        context.getStatistics().updateMaxHoldingSize(context.getHoldingPattern().size());
        // If runway available, schedule landing immediately
        if (context.isLandingRunwayAvailable()) {
            context.scheduleEvent(
                    new LandingEvent(eventTime, aircraft)
            );
        }
    }
}
