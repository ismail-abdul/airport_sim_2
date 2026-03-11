package com.airport_sim_2.events;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.events.LeaveHP;


// This events is dispatched to the UI
public class LeaveHPTest{

    private SimulationContext context;
    private Aircraft test;

    
    // Test if the aircraft leaves the holding pattern after the event
    @Test
    public void AircraftRemovedFromHPTest(){

        context = DummySimulation.setupContext();
        test = DummySimulation.createDummyAircraft();

        HoldingPattern holding_pattern = context.getHoldingPattern();

        // Add test aircraft to holding pattern and process leave holding pattern event
        context.getHoldingPattern().enqueue(test);
        LeaveHP event = new LeaveHP(100, test);

        event.process(context);

        // Holding pattern should not contain test aircraft
        assertFalse(context.getHoldingPattern().contains(test));

    }

}
