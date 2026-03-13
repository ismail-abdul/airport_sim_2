package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;
/**
 * This event describes an aircraft taking off from a specific runway.
 * This is event isn't necessary. 
 */
public class RunwayTakeOff extends RunwayEvent {
    private Aircraft aircraft;

    public RunwayTakeOff(Double eventTime, int runwayId, Aircraft aircraft) {
        super(eventTime, runwayId);
        this.aircraft =  aircraft;
    }

    @Override
    public EventType getType() {
        return EventType.RUNWAY_TAKEOFF;
    }

    @Override
    public void process(SimulationContext context) {
        // if (context.getTakeOffQueue().isEmpty()) {
        //     return;
        // }

        // Aircraft aircraft = context.getTakeOffQueue().dequeue();
        // context.getRunway(runwayId).occupy(aircraft);

        // double waitSeconds = eventTime - aircraft.getScheduledTime();
        // context.getStatistics().recordDepartureWait(waitSeconds);
        // Double releaseTime = eventTime + context.getTakeOffDuration();

        // context.scheduleEvent(new RunwayFreeEvent(releaseTime, runwayId));
    }

    @Override
    public void processEvent(SimulationEngine engine) {
        // Occupy the runway
        SimulationContext ctx = engine.getCtx();
        Runway r = ctx.getRunway(this.runwayId);
        assert (r != null): "Requested runway of id %d not found";
        r.occupy(aircraft);
        
        // Schedule a runway free event for after the takeoff is finsihed.
        RunwayFreeEvent event = new RunwayFreeEvent(eventTime + ctx.getTakeOffDuration(), runwayId);
        engine.enqueueEvent(event);
    }
    
}
