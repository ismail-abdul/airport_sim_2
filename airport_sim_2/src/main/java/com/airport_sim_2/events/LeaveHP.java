package com.airport_sim_2.events;
import java.time.LocalDateTime;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

// This events is dispatched to the UI
public class LeaveHP extends AbstractEvent {
    private final Aircraft aircraft;
    public LeaveHP(LocalDateTime eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {
        if (!context.getHoldingPattern().contains(aircraft)) {
            return; 
        }

        context.getHoldingPattern().remove(aircraft);
        int runwayId = context.findAvailableLandingRunway();
        context.getRunway(runwayId).occupy(aircraft);
    
        long nanosToAdd = (long) (context.getLandingDuration() * 60 * 1_000_000_000);
        LocalDateTime result = eventTime.plusNanos(nanosToAdd);
        context.scheduleEvent(new RunwayFreeEvent(result, runwayId));
    }
}
