package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.RunwayOpMode;

public class RunwayModeChangeEvent extends RunwayEvent {
    private final RunwayOpMode newMode;

    @Override
    public EventType getType() {
        return EventType.RUNWAY_OP_MODE_CHANGE;
    }

    public RunwayModeChangeEvent(double eventTime, int runwayId, RunwayOpMode newMode) {
        super(eventTime, runwayId); 
        this.newMode = newMode;
    }

    @Override
    public void process(SimulationContext context) {
        context.getRunway(runwayId).setMode(newMode);
        if (newMode == RunwayOpMode.TAKE_OFF || newMode == RunwayOpMode.MIXED_MODE && !context.getTakeOffQueue().isEmpty()) {
            context.scheduleEvent(new RunwayTakeOff(eventTime, runwayId));
        }
        if (newMode == RunwayOpMode.LANDING || newMode == RunwayOpMode.MIXED_MODE) {
            context.tryScheduleLanding(runwayId);
        }
    }
}

