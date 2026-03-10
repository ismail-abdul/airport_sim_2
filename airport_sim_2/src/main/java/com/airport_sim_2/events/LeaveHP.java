package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

// This events is dispatched to the UI
public class LeaveHP extends AbstractEvent {
    private final Aircraft aircraft;

    @Override
    public EventType getType() {
        return EventType.LEAVE_HP;
    }
    
    public LeaveHP(Double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext ctx) {

        /**
         * if (!context.getHoldingPattern().contains(aircraft)) {
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
         */
        /**
         * pop an element from the holding pattern
         */
        // Remove aircraft from holding pattern
        ctx.getHoldingPattern().remove(this.getAircraft());
    
        // Assign aircraft to an available landing runway
        Runway runway = ctx.getAvailableRunway();
    
        if (runway == null) {
            // If LeaveHP happens, there should always be runway available when processing.
            throw new IllegalStateException(
                "LeaveHP dispatched for " + this.getAircraft().getCallsign() + " but no landing runway available."
            );
        }
    
        /** 
        // Mark runway as occupied and assign aircraft
        runway.setOccupied(true);
        runway.setCurrentAircraft(this.getAircraft());
    
        // Schedule the landing completion event
        double landingCompleteTime = getTime().plusMinutes(ctx.getLandingDurationMinutes());
        ctx.getEventQueue().add(new LandingCompleteEvent(aircraft, runway.getId(), landingCompleteTime));
    
        // Record actual arrival time for statistics (delay = actual - scheduled)
        aircraft.setActualArrivalTime(getTime());
        */
    }

    @Override
    public int compareTo(Event event) {
        return this.getTime().compareTo(event.getTime());
    }

    public Aircraft getAircraft() {
        return aircraft;
    }
}
