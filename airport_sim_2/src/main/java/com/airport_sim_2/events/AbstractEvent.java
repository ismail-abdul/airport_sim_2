package com.airport_sim_2.events;
import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;

public abstract class AbstractEvent implements Event {

    protected final LocalDateTime eventTime;
    
    protected AbstractEvent(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public LocalDateTime getTime() {
        return eventTime;
    }

    @Override
    public int compareTo(Event other) {
        return this.eventTime.compareTo(other.getTime());
    }
 
    @Override
    public abstract void process(SimulationContext context);
}
