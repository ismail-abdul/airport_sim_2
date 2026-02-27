package com.airport_sim_2.simulation.events;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.simulation.SimulationContext;

public class RunwayModeChangeEvent extends RunwayEvent {
    private final RunwayOpMode newMode;
    public RunwayModeChangeEvent(double eventTime, int runwayId, RunwayOpMode newMode) {
        super(eventTime, runwayId); 
        this.newMode = newMode;
    }

    @Override
    public void process(SimulationContext context) {
        // change runway mode
        context.getRunway(runwayId).setMode(newMode);
        if (newMode == RunwayOpMode.TAKE_OFF || newMode == RunwayOpMode.MIXED) {
            if (!context.getTakeOffQueue().isEmpty() && context.isRunwayAvailable(runwayId)) {
                context.scheduleEvent(new TakeOffEvent(eventTime, runwayId));
            }
        }
        if (newMode == RunwayOpMode.LANDING || newMode == RunwayOpMode.MIXED) {
            context.tryScheduleLanding(runwayId);
        }
    }
}
