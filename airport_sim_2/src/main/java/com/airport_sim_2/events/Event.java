package com.airport_sim_2.events;
public interface Event extends Comparable<Event> {
    double getEventTime();
    void process(SimulationContext context);
}
