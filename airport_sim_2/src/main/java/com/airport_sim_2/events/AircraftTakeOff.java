package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;

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

    
}
