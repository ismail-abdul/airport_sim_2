package com.airport_sim_2.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

public class DiversionTest {
    private SimulationContext context;

    // Test to check if the diversion event removes the aircraft from the holding pattern
    @Test
    public void DiversionRemovesAircraftTest(){

        context = DummySimulationContext.setup();

        Aircraft test = DummySimulationContext.createDummyAircraft();

        context.getHoldingPattern().enqueue(test);

        // Check to see if the aircraft has been added to the holding pattern
        assertTrue(!context.getHoldingPattern().isEmpty());

        Diversion diversion = new Diversion(5.0, test);

        // process the diversion
        diversion.process(context);

        // Checks if aircraft has been removed and diverted counter incremented
        assertTrue(!context.getHoldingPattern().isEmpty());
        assertEquals(1, context.getStatistics().getDivertedCount());
    }
    
    
}
