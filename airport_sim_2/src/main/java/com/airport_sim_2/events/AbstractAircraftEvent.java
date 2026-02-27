package com.airport_sim_2.events;
import java.time.LocalDateTime;

import com.airport_sim_2.objects.Aircraft;

public abstract class AbstractAircraftEvent implements Event {
    private Aircraft aircraft;
    private LocalDateTime time;

    public AbstractAircraftEvent(Aircraft aircraft, LocalDateTime time) {
        this.time = time;
        this.aircraft = aircraft;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
