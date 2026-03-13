package com.airport_sim_2.events;

import org.junit.Test;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

public class RunwayTakeOffTest {
    
    private SimulationContext context;
    private Aircraft test;

    @Test
    public void RunwayTakeOffNormalTest(){
        // context = DummySimulation.setupContext();
        // test = DummySimulation.createDummyAircraft();

        // context.getTakeOffQueue().enqueue(test);
        // RunwayTakeOff event = new RunwayTakeOff(120.0, 1);
        // event.process(context);

        // assertTrue(context.getTakeOffQueue().isEmpty());
        // assertFalse(context.getRunway(1).isAvailableForTakeoff());

        // assertEquals(120.0,context.getStatistics().getAverageDepartureWait(),0.01);

        // There is another scehduled event
    }
}
