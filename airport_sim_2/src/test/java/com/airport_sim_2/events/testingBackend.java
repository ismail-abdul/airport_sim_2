package com.airport_sim_2.events;

import java.util.Arrays;

import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
//System.out.println("Simulation ended at time: " + currentTime);
public class testingBackend {
    private SimulationEngine engine;

    public testingBackend(){
        double currentTime = 0.0;
        engine = DummySimulation.setupEngine();
        Aircraft testAircraft = DummySimulation.createDummyAircraft(); // some issues with using engine.genNewAircraft

        EnterHP test_enter_hp_event = new EnterHP(100.0, testAircraft); // line 60 in simEngine has event time set to currenttime but timestamp could be better instead
        // also not sure why line 61 it enqueues aircraft when enterHP should be the event to enqueue it, might be LeaveHP instead

        currentTime = test_enter_hp_event.getTime();
        System.out.println("Time of the simulation after the event(should be 100.0): " + currentTime);
        test_enter_hp_event.processEvent(engine);

        System.out.println("Callsigns of aircrafts in HP (should be [\"TEST\"]): " + Arrays.toString(engine.getCtx().getHoldingPattern().getCallsign()));
        System.out.println("Holding pattern size after adding one aircraft (should be 1): " + engine.getCtx().getHoldingPattern().size());
        System.out.println("Event type added after aircraft enter HP (should be landing): " + engine.getNextEvent().getType().toString());
        System.out.println("Max HP size (should be 1): " + engine.getCtx().getStatistics().getMaxHoldingSize());
        
        engine.removeEvent(engine.getNextEvent()); // clear the event queue

        Landing test_landing_event = new Landing(200.0, testAircraft, 1); // not sure why runwayID is needed for Landing constructor

        test_landing_event.processEvent(engine);

        System.out.println("Test aircraft actual time (should be 100.0): " + testAircraft.getActualTime().toString());
        System.out.println("Call sign of aircraft in runway 1 (should be [\"TEST\"]): " + engine.getCtx().getRunway(1).getCurrentAircraft().getCallsign());
        System.out.println("Event type added after landing aircraft (should be Runway_Free): " + engine.getNextEvent().getType().toString());

        RunwayFreeEvent test_free_runway_event = (RunwayFreeEvent) engine.getNextEvent(); // should be a free runway event next so casting should be fine
        test_free_runway_event.processEvent(engine);
        System.out.println("Runway current aircraft (should be null): " + engine.getCtx().getRunway(1).getCurrentAircraft());
        
        // NO STATS RECORDED FOR AIRCRAFTS LANDING

        AircraftTakeOff test_take_off_event = new AircraftTakeOff(testAircraft, 400.0);
        engine.getCtx().getTakeOffQueue().enqueue(testAircraft);
        test_take_off_event.processEvent(engine);

        System.out.println("Test aircraft actual time (should be 400.0): " + testAircraft.getActualTime().toString());
        System.out.println("Call sign of aircraft in runway 1 (should be [\"TEST\"]): " + engine.getCtx().getRunway(1).getCurrentAircraft().getCallsign());
        System.out.println("Event type added after landing aircraft (should be Runway_Free): " + engine.getNextEvent().getType().toString());
        // Test aircraft does not leave the take-off queue

        // NO STATS RECORDED FOR AIRCRAFTS LEAVING

    }
    
}
