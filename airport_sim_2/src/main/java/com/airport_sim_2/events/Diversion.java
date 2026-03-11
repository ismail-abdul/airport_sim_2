package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;

/**
 * This event describes the act where an aircraft must leave the holding pattern. 
 * This is often due to fuel level being too low or an emergency status being found. Also emergency status shouldn't be acquired and then lost; it should be maintained during the lifetime of an Aircraft object. Also emergency status should only really happen to planes that are in the holding pattern. 
 */
public class Diversion extends AbstractEvent {

    private final Aircraft aircraft; 

    /**
     * @param eventTime Describes when this event should occur if carried out
     * @param aircraft Which aircraft is being diverted
    */
    public Diversion(Double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public EventType getType() {
        return EventType.DIVERSION;
    }

    @Override
    public void process(SimulationContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processEvent(SimulationEngine engine) {      
        // check if there is an aircraft to divert  
        if (!engine.getCtx().getHoldingPattern().remove(aircraft)) {
            return;
        }

        // remove aircraft from Holding Pattern
        engine.getCtx().getHoldingPattern().remove(aircraft);

        // record diversion stats
        engine.getCtx().getStatistics().incrementDiverted();

        // mark aircraft as diverted
        aircraft.setActualTime(engine.getCurrentTime());

        // debugging
        System.out.println("Aircraft " + aircraft.getCallsign() + "diverted at time" + engine.getCurrentTime());
    }


}
