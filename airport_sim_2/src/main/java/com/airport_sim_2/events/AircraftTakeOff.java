package com.airport_sim_2.events;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.controller.TimeSeriesPoint;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

/**
 * This event is used to schedule take off that will happen in the future. 
 */
public class AircraftTakeOff extends AbstractEvent {

    private Aircraft aircraft;

    public AircraftTakeOff(Aircraft aircraft, double eventTime) {
        super(eventTime);
        this.aircraft = aircraft;

    }

    @Override
    public EventType getType() {
        return EventType.AC_TAKEOFF;
    }

    // NOTE - Kishor will do processEvent for this one 

    @Override
    public void process(SimulationContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    private void scheduleNextDeparture(SimulationEngine engine) {
        java.util.Random random = new java.util.Random();
        double lambda = 1.0 / 60.0; // 1 departure per 60 simulated seconds
        double interDepartureTime = -Math.log(1.0 - random.nextDouble()) / lambda;
        double targetTime = engine.getCurrentTime() + interDepartureTime;
        double jitter = random.nextGaussian() * (5 * 60);
        double nextTime = Math.max(engine.getCurrentTime() + 1, targetTime + jitter);
        if (nextTime <= engine.getEndTime()) {
            Aircraft nextAircraft = SimulationEngine.genNewAircraft(nextTime);
            engine.getCtx().getTakeOffQueue().enqueue(nextAircraft);
            engine.enqueueEvent(new AircraftTakeOff(nextAircraft, nextTime));
        }
    }

    @Override
    public void processEvent(SimulationEngine engine) {
        // Guard: if this aircraft was already processed by a RunwayFreeEvent, skip.
        if (!engine.getCtx().getTakeOffQueue().contains(aircraft)) {
            return;
        }

        Runway runway = engine.getCtx().getAvailableTakeOffRunway();

        if (this.eventTime - aircraft.getScheduledTime() > engine.getCtx().getMaxWaitTime()) {
            // Cancel the flight.
            engine.getCtx().getTakeOffQueue().dequeue();

            // Record a cancellation in the stats collector.
            StatisticsCollector stats = engine.getCtx().getStatistics();
            stats.incrementCancelled();
            TimeSeriesPoint p = new TimeSeriesPoint(eventTime, stats.getCancelledCount());
            stats.cancellation_ts_add(p);

            // Record a wait time (of max wait time) in the stats collector.
            TimeSeriesPoint d = new TimeSeriesPoint(eventTime, engine.getCtx().getMaxWaitTime());
            stats.delay_ts_add(d);

            // Poisson chain: schedule next departure arrival.
            scheduleNextDeparture(engine);
            return;
        }

        if (runway != null) {
            // Runway is available to perform takeoff
            assert runway.isAvailableForTakeoff();

            this.aircraft.setActualTime(engine.getCurrentTime());
            runway.occupy(this.aircraft);
            engine.getCtx().getTakeOffQueue().dequeue();

            // Record departure
            StatisticsCollector stats = engine.getCtx().getStatistics();
            double waitSeconds = Math.max(0, engine.getCurrentTime() - aircraft.getScheduledTime());
            stats.recordDepartureWait(waitSeconds / 60.0);
            stats.incrementDeparted();
            stats.departure_ts_add(new TimeSeriesPoint(engine.getCurrentTime(), stats.getDepartedCount()));
            stats.toq_ts_add(new TimeSeriesPoint(engine.getCurrentTime(), engine.getCtx().getTakeOffQueue().size()));

            // schedule runway release
            RunwayFreeEvent event = new RunwayFreeEvent(this.eventTime + engine.getCtx().getTakeOffDuration(), runway.getId());
            engine.enqueueEvent(event);

            // Poisson chain: schedule next departure arrival.
            scheduleNextDeparture(engine);

        } else {
            // No runway available therefore we need to find the earliest release
            double earliestTime = Double.MAX_VALUE;
            for (Runway r : engine.getCtx().getRunways()) {
                Aircraft currentAircraft = r.getCurrentAircraft();
                if (currentAircraft == null) {
                    continue;
                }

                // changed from scheduled time to engine current time
                double freeTime = engine.getCurrentTime() + engine.getCtx().getTakeOffDuration();
                if (freeTime < earliestTime) {
                    earliestTime = freeTime;
                }
            }

            // Reschedule takeoff
            double retryTime = Math.max(earliestTime, engine.getCurrentTime() + 1);
            if (retryTime <= engine.getEndTime()) {
                AircraftTakeOff retry = new AircraftTakeOff(this.aircraft, retryTime);
                engine.enqueueEvent(retry);
            }
        }
    }
}
