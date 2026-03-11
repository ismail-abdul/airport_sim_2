package com.airport_sim_2.events;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.RunwayOpMode;

public class RunwayModeChangeEventTest {
    private SimulationContext context;
    // private SimulationEngine engine;

    @Test
    public void RunwayModeChangeTest() {
        context = DummySimulation.setupContext();
        // engine = DummySimulation.setupEngine();

        context.getRunway(1).setMode(RunwayOpMode.LANDING);

        RunwayModeChangeEvent take_off_mode_event = new RunwayModeChangeEvent(100,1, RunwayOpMode.TAKE_OFF);
        take_off_mode_event.process(context);
        assertEquals(RunwayOpMode.TAKE_OFF,context.getRunway(1).getMode());


        RunwayModeChangeEvent landing_mode_event = new RunwayModeChangeEvent(100,1, RunwayOpMode.LANDING);
        landing_mode_event.process(context);
        assertEquals(RunwayOpMode.LANDING,context.getRunway(1).getMode());


    }

    // Do same for mixed and possibly test if new events are scheduled
}

