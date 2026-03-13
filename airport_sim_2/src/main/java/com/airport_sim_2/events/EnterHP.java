package com.airport_sim_2.events;

import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.queues.HoldingPattern;

public class EnterHP extends AbstractEvent {

    private final Aircraft aircraft;
    private final double failure_rate = 0.05;

    @Override
    public EventType getType() {
        return EventType.ENTER_HP;
    }
    
    public EnterHP(Double eventTime, Aircraft aircraft) {
        super(eventTime);
        this.aircraft = aircraft;
    }

    @Override
    public void process(SimulationContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processEvent(SimulationEngine engine) {
        // add aircraft to the holding pattern
        HoldingPattern hp = engine.getCtx().getHoldingPattern();
        hp.enqueue(aircraft);

        // update statistics
        engine.getCtx().getStatistics().updateMaxHoldingSize(hp.size());
        engine.getCtx().getStatistics().hp_ts_add(
            new com.airport_sim_2.controller.TimeSeriesPoint(engine.getCurrentTime(), hp.size())
        );

        // check if a runway is available for landing
        int runwayId = engine.getCtx().findAvailableLandingRunway();
        if (runwayId != -1) {
            Landing landing = new Landing(engine.getCurrentTime(), aircraft, runwayId);
            engine.enqueueEvent(landing);
        }

        // Schedule the next arrival using a Poisson process (1 aircraft per simulated minute)
        java.util.Random random = new java.util.Random();
        double lambda = 1.0 / 60.0; // 1 arrival per 60 seconds
        double interArrivalTime = -Math.log(1.0 - random.nextDouble()) / lambda;
        double targetTime = engine.getCurrentTime() + interArrivalTime;
        double jitter = random.nextGaussian() * (5 * 60);
        double nextTime = Math.max(engine.getCurrentTime() + 1, targetTime + jitter);
        if (nextTime <= engine.getEndTime()) {
            Aircraft nextAircraft = SimulationEngine.genNewAircraft(nextTime);
            EnterHP event = new EnterHP(nextTime, nextAircraft);
            engine.enqueueEvent(event);
        }
    }
}
