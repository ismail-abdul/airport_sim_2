package com.airport_sim_2.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;

public class DiversionTest {
    private SimulationEngine engine;
    

    // Test to check if the diversion event removes the aircraft from the holding pattern
    @Test
    public void DiversionRemovesAircraftTest(){

        engine = DummySimulation.setupEngine();

        Aircraft test = DummySimulation.createDummyAircraft();

        engine.getCtx().getHoldingPattern().enqueue(test);

        // Check to see if the aircraft has been added to the holding pattern
        assertTrue(!engine.getCtx().getHoldingPattern().isEmpty());

        Diversion diversion = new Diversion(5.0, test);

        // process the diversion
        diversion.processEvent(engine);
        double diverted_time = engine.getCurrentTime();

        // Checks if aircraft has been removed and diverted counter incremented
        assertTrue(!engine.getCtx().getHoldingPattern().isEmpty());
        assertEquals(1, engine.getCtx().getStatistics().getDivertedCount());
        assertEquals(test.getActualTime(), diverted_time, 0.01);
    }
    
    // Test to check if the diversion event removes the aircraft from the holding pattern
    @Test
    public void DiversionWithNoAircraftTest(){

        engine = DummySimulation.setupEngine();

        Aircraft test = DummySimulation.createDummyAircraft();

        // Check to see if there is no aircraft in the holding pattern
        assertTrue(engine.getCtx().getHoldingPattern().isEmpty());

        Diversion diversion = new Diversion(5.0, test);

        // process the diversion
        diversion.processEvent(engine);

        // Checks if there is still nothing in holding pattern and diverted counter not changed
        assertTrue(engine.getCtx().getHoldingPattern().isEmpty());
        assertEquals(0, engine.getCtx().getStatistics().getDivertedCount());
    }

}
