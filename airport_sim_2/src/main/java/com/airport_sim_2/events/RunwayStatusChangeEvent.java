package com.airport_sim_2.events;


import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOperationalStatus;

/**
 * Describes a change to <i>Runway Status</i> which
 *  describes what is happening to the runwa e.g. inspection, snow clearance etc.
 */
public class RunwayStatusChangeEvent extends RunwayEvent {
    private final RunwayOperationalStatus newStatus;

    public RunwayStatusChangeEvent(Double eventTime, int runwayId, RunwayOperationalStatus newStatus) {
        super(eventTime, runwayId);
        this.newStatus = newStatus;
    }

    @Override
    public EventType getType() {
        return EventType.RUNWAY_OP_STATUS_CHANGE;
    }

    @Override
    public void process(SimulationContext context) {
        context.getRunway(runwayId).setStatus(newStatus);
    }

    /**
        If occupied, reschedule this event. Since it is difficult to get when the runway will next be free,
        we'll reschedule to a time after the runway is free. </br>
        If Runway class changes later, we'll can consider the exact time when the runway becomes free.
     */
    @Override
    public void processEvent(SimulationEngine engine) {
        SimulationContext ctx = engine.getCtx();
        Runway r = ctx.getRunway(runwayId);
        double newTime = eventTime;
        if (r.isOccupied()) {
            // reschedule this event. Since it is difficult to get when the runway will next be free
            // We'll reschedule to a time after the runway is free. 
            // If Runway class changes later, we'll can consider the exact time when the runway becomes free.
            switch (r.getMode()) {
                case TAKE_OFF, MIXED_MODE ->  {
                    newTime += ctx.getLandingDuration(); // 20
                }
                case LANDING -> {
                    newTime += ctx.getLandingDuration(); // 15
                }
                default -> System.out.println("Unimplemented handler for this ENUM value");
            }
            RunwayStatusChangeEvent event = new RunwayStatusChangeEvent(newTime, runwayId, newStatus);
            engine.enqueueEvent(event);
        } else {
            r.setStatus(newStatus);
        }
    }
}
