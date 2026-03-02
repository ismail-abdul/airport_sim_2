package com.airport_sim_2.events;
import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;

public class RunwayFreeEvent extends RunwayEvent {
    public RunwayFreeEvent(LocalDateTime eventTime, int runwayId) {
        super(eventTime, runwayId);
    }
    @Override
    public void process(SimulationContext context) {

        // context.getRunway(runwayId).setOccupied(false);
        // // try to schedule next takeoff
        // if (!context.getTakeOffQueue().isEmpty()) {
        //     context.scheduleEvent(new TakeOffEvent(eventTime, runwayId));
        // }
        // // try to schedule landing
        // context.tryScheduleLanding(runwayId);
    }
    
}
