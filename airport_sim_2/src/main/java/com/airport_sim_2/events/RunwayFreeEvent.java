package com.airport_sim_2.simulation.events;
import com.airport_sim_2.simulation.SimulationContext;
public class RunwayFreeEvent extends RunwayEvent {
    public RunwayFreeEvent(double eventTime, int runwayId) {
        super(eventTime, runwayId);
    }
    @Override
    public void process(SimulationContext context) {

        context.getRunway(runwayId).setOccupied(false);
        // try to schedule next takeoff
        if (!context.getTakeOffQueue().isEmpty()) {
            context.scheduleEvent(new TakeOffEvent(eventTime, runwayId));
        }
        // try to schedule landing
        context.tryScheduleLanding(runwayId);
    }
    
}
