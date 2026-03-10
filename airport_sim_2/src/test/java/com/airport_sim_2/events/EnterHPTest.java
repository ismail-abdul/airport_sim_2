package com.airport_sim_2.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;

public class EnterHPTest {
    private SimulationContext context;

    // Test to check aircraft is added to holding pattern then moved into the runway
    @Test
    public void AircraftEntersFromHPToEmptyRunwayTest(){

        context = DummySimulationContext.setup();

        Aircraft test = DummySimulationContext.createDummyAircraft();

        EnterHP enter_hp = new EnterHP(5.0, test);

        // process the events
        enter_hp.process(context);
        context.getNextEvent().process(context);


        // Checks if aircraft is in runway and holding pattern is still empty
        assertTrue(context.getHoldingPattern().isEmpty());
        assertEquals(test.getCallsign(), context.getRunway(1).getCurrentAircraft().getCallsign());
    }

    // Test to check aircraft is added to holding pattern and stays because runways are occupied
    @Test
    public void AircraftEntersFromHPToFullRunwayTest(){

        // Fill the runway
        context = DummySimulationContext.setup();
        context.getRunway(1).occupy(new Aircraft("TEMPORARY", "OP", "ORG", "DST", 200.0f, 1000.0f, 100.0f, AircraftStatus.NORMAL, 0.0));

        // Aircraft to add to holding pattern
        Aircraft test = DummySimulationContext.createDummyAircraft();

        EnterHP enter_hp = new EnterHP(5.0, test);

        // process the event
        enter_hp.process(context);

        // Check callsigns match in holding pattern and runway with the original objects
        assertEquals(test.getCallsign(), context.getHoldingPattern().getCallsign()[0]);
        assertEquals("TEMPORARY", context.getRunway(1).getCurrentAircraft().getCallsign());
    }
    
    
}
