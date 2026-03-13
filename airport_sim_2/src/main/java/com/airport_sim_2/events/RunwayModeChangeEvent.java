package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;


public class RunwayModeChangeEvent extends RunwayEvent {
    private final RunwayOpMode newMode;
    public final static double duration = 60; 

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
        // context.getRunway(runwayId).setMode(newMode);
        // if (newMode == RunwayOpMode.TAKE_OFF || newMode == RunwayOpMode.MIXED_MODE && !context.getTakeOffQueue().isEmpty()) {
        //     context.scheduleEvent(new RunwayTakeOff(eventTime, runwayId));
        // }
        // if (newMode == RunwayOpMode.LANDING || newMode == RunwayOpMode.MIXED_MODE) {
        //     context.tryScheduleLanding(runwayId);
        // }
    }

    @Override
    public void processEvent(SimulationEngine engine) {
        // Should schedule a runway free event. 
        // Or you allow the engine itself to generate the event.
        // Although that seems less sensible since you know exactly what event you need to generate here.
        SimulationContext ctx = engine.getCtx();
        Runway runway = ctx.getRunway(this.runwayId);
        Aircraft dummy = SimulationEngine.genNewAircraft(null);
        runway.occupy(dummy);
        // Update runway operational mode
        runway.setMode(newMode);
        RunwayFreeEvent event = new RunwayFreeEvent(eventTime+duration, this.runwayId);
        engine.enqueueEvent(event);
    }

    public static double getDuration() {
        return duration;
    }
}

