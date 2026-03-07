package com.airport_sim_2.events;

import com.airport_sim_2.model.SimulationContext;

public abstract class AbstractEvent implements Event {

    protected Double eventTime;
    
    protected AbstractEvent(double eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public Double getTime() {
        return eventTime;
    }

    @Override
    public int compareTo(Event other) {
        return this.eventTime.compareTo(other.getTime());
    }
 
    @Override
    public abstract void process(SimulationContext context);
}
