package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

/**
 * This event is used to schedule take off that will happen in the future. 
 */
public class AircraftTakeOff extends AbstractEvent {

    private Aircraft aircraft;

    public AircraftTakeOff(Aircraft aircraft, double eventTime) {
        super(eventTime);
        this.aircraft = aircraft;

    }

    @Override
    public EventType getType() {
        return EventType.AC_TAKEOFF;
    }

    // NOTE - Kishor will do processEvent for this one 

    @Override
    public void process(SimulationContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    @Override 
    public void processEvent(SimulationEngine engine) {
        Runway runway = engine.getCtx().getAvailableRunway();

        if (runway != null) {
            // Runway is available to perform takeoff
            assert runway.isAvailableForTakeoff();

        this.aircraft.setActualTime(engine.getCurrentTime());
        runway.occupy(this.aircraft);

        // schedule runway release
        RunwayFreeEvent event = new RunwayFreeEvent(this.eventTime + engine.getCtx().getTakeOffDuration(), runway.getId());
        engine.enqueueEvent(event);

        } else {
            // No runway available therefore we need to find the earliest release
            double earliestTime = Double.MAX_VALUE;
            for (Runway r : engine.getCtx().getRunways()) {
                Aircraft currentAircraft = r.getCurrentAircraft();
                if (currentAircraft == null) {
                    continue;
                }

                double freeTime = currentAircraft.getScheduledTime() + engine.getCtx().getTakeOffDuration();
                if (freeTime < earliestTime) {
                    earliestTime = freeTime;
                }
            }

            // Reschedule takeoff
            AircraftTakeOff retry = new AircraftTakeOff(this.aircraft, earliestTime);
            engine.enqueueEvent(retry);
        }
    }
}
