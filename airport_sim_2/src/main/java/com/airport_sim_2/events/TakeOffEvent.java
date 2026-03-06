package com.airport_sim_2.events;
import java.time.Duration;
import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

public class TakeOffEvent extends RunwayEvent {
    public TakeOffEvent(LocalDateTime eventTime, int runwayId) {
        super(eventTime, runwayId);
    }

    @Override
    public void process(SimulationContext context) {
        if (context.getTakeOffQueue().isEmpty()) {
            return;
        }

        Aircraft aircraft = context.getTakeOffQueue().dequeue();
        context.getRunway(runwayId).occupy(aircraft);

        long waitMinutes = Duration.between(aircraft.getScheduledTime(), eventTime).toMinutes();
        context.getStatistics().recordDepartureWait(waitMinutes);
        LocalDateTime releaseTime = eventTime.plusMinutes(context.getTakeOffDuration());

        context.scheduleEvent(new RunwayFreeEvent(releaseTime, runwayId));
    }
}
