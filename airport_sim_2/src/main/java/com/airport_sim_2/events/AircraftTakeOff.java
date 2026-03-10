package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

/**
 * This event describe a specific aircraft actually taking off from the 
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

    
}
