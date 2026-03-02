package com.airport_sim_2.events;

import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;

// This events is dispatched to the UI
public class LeaveHP extends AbstractAircraftEvent { 
    public LeaveHP(Aircraft aircraft, LocalDateTime time){
        super(aircraft, time);
    }

    @Override
    public void process(SimulationContext ctx) {

    }

    @Override
    public int compareTo(Event event) {
        return this.getTime().compareTo(event.getTime());
    }
}
