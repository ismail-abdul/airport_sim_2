package com.airport_sim_2.events;
import java.time.Duration;
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
        if (runwayId == -1) {
            return;
        }
        context.getHoldingPattern().remove(aircraft);
        context.getRunway(runwayId).occupy(aircraft);
        long waitMinutes = Duration.between(aircraft.getScheduledTime(), eventTime).toMinutes();
        context.getStatistics().recordArrivalWait(waitMinutes);
    
        LocalDateTime releaseTime = eventTime.plusMinutes(context.getLandingDuration());
        context.scheduleEvent(new RunwayFreeEvent(releaseTime, runwayId));
    }
}
