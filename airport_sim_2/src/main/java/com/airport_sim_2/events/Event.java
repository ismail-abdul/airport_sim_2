package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;

public interface Event extends Comparable<Event> {
    EventType getType();
    Double getTime();
    void process(SimulationContext context);
}
