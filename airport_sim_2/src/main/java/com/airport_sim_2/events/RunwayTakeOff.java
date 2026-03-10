package com.airport_sim_2.events;
import com.airport_sim_2.model.EventType;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
/**
 * This event describes an aircraft taking off from a specific runway.
 * Which aircraft is irrelevant. We are simply taking the highest priority departure and executing.
 */
public class RunwayTakeOff extends RunwayEvent {
    
    public RunwayTakeOff(Double eventTime, int runwayId) {
        super(eventTime, runwayId);
    }

    @Override
    public EventType getType() {
        return EventType.RUNWAY_TAKEOFF;
    }

    @Override
    public void process(SimulationContext context) {
        if (context.getTakeOffQueue().isEmpty()) {
            return;
        }

        Aircraft aircraft = context.getTakeOffQueue().dequeue();
        context.getRunway(runwayId).occupy(aircraft);

        double waitSeconds = eventTime - aircraft.getScheduledTime();
        context.getStatistics().recordDepartureWait(waitSeconds);
        Double releaseTime = eventTime + context.getTakeOffDuration();

        context.scheduleEvent(new RunwayFreeEvent(releaseTime, runwayId));
    }
}
