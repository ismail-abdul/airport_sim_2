package com.airport_sim_2.events;
import com.airport_sim_2.model.SimulationContext;

public abstract class AbstractEvent implements Event {

    protected final double eventTime;

    protected AbstractEvent(double eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public double getEventTime() {
        return eventTime;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.eventTime, other.getEventTime());
    }
 
    @Override
    public abstract void process(SimulationContext context);
}
