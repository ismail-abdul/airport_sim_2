package com.airport_sim_2.events;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.RunwayOperationalStatus;
;


public class RunwayStatusChangeEventTest {
    private SimulationContext context;

    // Test for changing runway status to equipemnt failure
    @Test
    public void RunwayStatusChangeToEquipmentFailureTest(){

        context = DummySimulation.setupContext();

        RunwayStatusChangeEvent event = new RunwayStatusChangeEvent(10.0, 1, RunwayOperationalStatus.EQUIPMENT_FAILURE);
        
        assertEquals(RunwayOperationalStatus.AVAILABLE,context.getRunway(1).getStatus());

        event.process(context);

        assertEquals(RunwayOperationalStatus.EQUIPMENT_FAILURE,context.getRunway(1).getStatus());
    }

    // Test for changing runway status to snow clearance
    @Test
    public void RunwayStatusChangeToSnowClearanceTest(){

        context = DummySimulation.setupContext();

        RunwayStatusChangeEvent event = new RunwayStatusChangeEvent(10.0, 1, RunwayOperationalStatus.SNOW_CLEARANCE);
        
        assertEquals(RunwayOperationalStatus.AVAILABLE,context.getRunway(1).getStatus());

        event.process(context);

        assertEquals(RunwayOperationalStatus.SNOW_CLEARANCE,context.getRunway(1).getStatus());
    }

    // Test for changing runway status to inspection
    @Test
    public void RunwayStatusChangeToInspectionTest(){

        context = DummySimulation.setupContext();

        RunwayStatusChangeEvent event = new RunwayStatusChangeEvent(10.0, 1, RunwayOperationalStatus.INSPECTION);
        
        assertEquals(RunwayOperationalStatus.AVAILABLE,context.getRunway(1).getStatus());

        event.process(context);

        assertEquals(RunwayOperationalStatus.INSPECTION,context.getRunway(1).getStatus());
    }
    
    
}
