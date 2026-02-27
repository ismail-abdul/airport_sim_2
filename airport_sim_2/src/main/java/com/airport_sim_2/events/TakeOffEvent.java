package com.airport_sim_2.events;
import com.airport_sim_2.objects.Aircraft;
import java.time.LocalDateTime;

public class TakeOffEvent extends RunwayEvent {
    public TakeOffEvent(double eventTime, int runwayId) {
        super(eventTime, runwayId);
    }
    @Override
    public void process(SimulationContext context) {
        // if no aircraft waiting, do nothing
        if (context.getTakeOffQueue().isEmpty()) {
            return;
        }
        // if runway unavailable, do nothing
        if (!context.isRunwayAvailable(runwayId)) {
            return;
        }
        // get aircraft from queue (FIFO)
        Aircraft aircraft = context.getTakeOffQueue().dequeue();
        // mark runway as occupied
        context.getRunway(runwayId).setOccupied(true);
        // record wait time
        double waitTime = eventTime - aircraft.getScheduledTime();
        context.getStatistics().recordDepartureWait(waitTime);
        // schedule runway release after fixed takeoff duration
        double runwayReleaseTime = eventTime + context.getTakeOffDuration();
        context.scheduleEvent(new RunwayFreeEvent(runwayReleaseTime, runwayId));
    }
}
