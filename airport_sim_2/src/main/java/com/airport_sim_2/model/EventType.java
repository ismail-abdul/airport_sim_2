package com.airport_sim_2.model;

/**
 * EventType is used to describe an event before it's generation. 
 * Some of these event types are here to be pre-emptively scheduled. 
 * 
 * Simulation animations can be coordinated according to time before next event. yfm. 
 */
public enum EventType {
    TAKEOFF, 
    LANDING, 

    
    AIRCRAFT_EM_STATUS_CHANGE, // might break down to specific changes
    RUNWAY_OP_MODE_CHANGE, // might break down to specific changes
    RUNWAY_OP_STATUS_CHANGE, // might break down to specifc changes
    
    CRITICAL_FUEL_LEVEL, // these will be scheduled when an aircraft enters the holding pattern. In fact, they also need to have their fuel levels randomly generated. In fact, enter holding pattern generation needs to be automated as well. 
    TAKEOFF_CANCELLATION, // these can be destroyed, if cancellation can be avoided
    
    DIVERSION, // preemptively scheduled, then destroyed if landing processed before diversion. Could also be processed because holding pattern is full.   
    ENTER_HP,
}
