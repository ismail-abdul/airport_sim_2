package com.airport_sim_2.events;

// This events is dispatched to the UI
public class LeaveHP extends AbstractAircraftEvent { 
    public LeaveHP(Aircraft aircraft, LocalDateTime time){
        super(aircraft, time);
    }
}
