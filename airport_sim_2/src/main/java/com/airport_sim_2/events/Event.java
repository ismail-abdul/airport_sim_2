package com.airport_sim_2.events;
import com.airport_sim_2.model.SimulationContext;

public interface Event extends Comparable<Event> {
    Double getTime();
    void process(SimulationContext context);
}
