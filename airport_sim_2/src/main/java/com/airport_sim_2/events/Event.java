package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;

public interface Event extends Comparable<Event> {
    EventType getType();
    Double getTime();
    void process(SimulationContext context);
    void processEvent(SimulationEngine engine);
}
