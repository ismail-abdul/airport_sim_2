package com.airport_sim_2.events;
import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;

public interface Event extends Comparable<Event> {
    LocalDateTime getTime();
    void process(SimulationContext context);
}
