package com.airport_sim_2.events;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

/* What information does the Landing Class need?
Keep in mind seperation of concerns from the controller. 

Aircraft reference
Runway reference
timestamp etc
*/

public class Landing extends AbstractEvent   {
    private final Aircraft aircraft;
    private final int runwayID;
    
    public Landing(Double eventTime, Aircraft aircraft, int runwayID) {
        super(eventTime);
        this.aircraft = aircraft;
        this.runwayID = runwayID;
    }

    @Override
    public void process(SimulationContext context) {
        // check for a stale event as the aircraft may have already been diverted
        if (!context.getHoldingPattern().contains(aircraft)) {
            return;
        }

        Runway runway = context.getRunway(runwayID);
        // safety check
        if (!runway.isAvailableForLanding()) {
            return;
        }

        // remove from holding
        context.getHoldingPattern().remove(aircraft);
        // occupy runway
        runway.occupy(aircraft);
        // record arrival wait time
        Double waitMinutes = (eventTime - aircraft.getScheduledTime()) / 60;
        context.getStatistics().recordArrivalWait(waitMinutes);
        // schedule runway release
        Double releaseTime = (double) eventTime + context.getLandingDuration();
        context.scheduleEvent(new RunwayFreeEvent(releaseTime, runwayID));
    }
}
