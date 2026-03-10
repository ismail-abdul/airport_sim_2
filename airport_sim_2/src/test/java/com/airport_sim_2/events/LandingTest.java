package com.airport_sim_2.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import com.airport_sim_2.objects.Runway;

public class LandingTest {
    private SimulationContext context;

    // Test to check if Landing the aircraft with free runways works
    @Test
    public void landingEventTest(){

        context = DummySimulationContext.setup();
        Aircraft test = DummySimulationContext.createDummyAircraft();
        Runway runway = context.getRunway(1);

        context.getHoldingPattern().enqueue(test);
        Landing landing = new Landing(120.0, test,1);
        
        landing.process(context);

        // Check the holding pattern removed test aircraft and runway not available for landing
        assertFalse(context.getHoldingPattern().contains(test));
        assertFalse(runway.isAvailableForLanding());
        
        // Check if stats updated correctly
        StatisticsCollector stats = context.getStatistics();
        assertEquals(2.0, stats.getAverageArrivalWait(),0.001);
        assertEquals(2.0, stats.getMaxWaitTimeRecorded(),0.001);

        // Check if free runway event scheduled
        assertTrue(context.hasMoreEvents());
    }
    
    // Test to check if Landing the aircraft with full runways does nothing
    @Test
    public void cancelLandingEventTest(){

        context = DummySimulationContext.setup();
        Aircraft test = DummySimulationContext.createDummyAircraft();
        Runway runway = context.getRunway(1);
        context.getRunway(1).occupy(new Aircraft("TEMPORARY", "OP", "ORG", "DST", 200.0f, 1000.0f, 100, AircraftStatus.NORMAL, 0.0));


        context.getHoldingPattern().enqueue(test);
        Landing landing = new Landing(120.0, test,1);
        
        landing.process(context);

        // Check the holding pattern still contains test aircraft and runway is still not available for landing
        assertTrue(context.getHoldingPattern().contains(test));
        assertFalse(runway.isAvailableForLanding());
    }
    
}
