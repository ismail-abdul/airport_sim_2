package com.airport_sim_2.events;

import java.util.ArrayList;
import java.util.List;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.AircraftStatus;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;

public class DummySimulation {

    //Helper method to create a dummy aircraft for our tests
    public static Aircraft createDummyAircraft() {
        return new Aircraft("TEST", "OP", "ORG", "DST", 200.0f, 1000.0f, 100, AircraftStatus.NORMAL, 0.0);
    }

    // Helper method to create a dummy simulation context with empty holding patterns and takeoff queues
    public static SimulationContext setupContext(){
        HoldingPattern holding_pattern = new HoldingPattern();
        TakeOffQueue take_off_queue = new TakeOffQueue();
        StatisticsCollector stats_collector = new StatisticsCollector();
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(1, RunwayOpMode.MIXED_MODE, RunwayOperationalStatus.AVAILABLE));
        return new SimulationContext(holding_pattern, take_off_queue, runways, stats_collector);
    }

    // Helper method to create a dummy simulation context with empty holding patterns and takeoff queues
    public static SimulationEngine setupEngine(){
        return new SimulationEngine(1000.0);
    }
}
