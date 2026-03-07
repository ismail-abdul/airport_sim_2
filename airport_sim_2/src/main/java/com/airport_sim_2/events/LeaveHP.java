package com.airport_sim_2.events;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

// This events is dispatched to the UI
public class LeaveHP extends AbstractEvent {
    private final Aircraft aircraft;
    public LeaveHP(Double eventTime, Aircraft aircraft) {
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
        Double waitSeconds = eventTime - aircraft.getScheduledTime();
        context.getStatistics().recordArrivalWait(waitSeconds);
    
        Double releaseTime = eventTime + context.getLandingDuration();
        context.scheduleEvent(new RunwayFreeEvent(releaseTime, runwayId));
    }
}
