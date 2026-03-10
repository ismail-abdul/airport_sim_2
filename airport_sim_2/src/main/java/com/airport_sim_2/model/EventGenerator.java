package com.airport_sim_2.model;

import java.util.Random;

import com.airport_sim_2.events.EnterHP;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.events.Landing;
import com.airport_sim_2.events.LeaveHP;
import com.airport_sim_2.events.RunwayModeChangeEvent;
import com.airport_sim_2.events.RunwayStatusChangeEvent;
import com.airport_sim_2.events.RunwayTakeOff;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;


/**
 * Generate a specific type of event that is statistically independent from the generation of other events.
 */
public class EventGenerator {
    private Random random;
    private final EventType eventType;
    private double stdDev;
    private double mean;
    
    /**
     * @param eventType The type of event this generator creates
     * @param mean Mean time interval between events (in your time units)
     * @param stdDev Standard deviation of the interval
     */
    public EventGenerator(EventType eventType, double mean, double stdDev) {
        this.eventType = eventType;
        this.random = new Random();
        this.mean = mean;
        this.stdDev = stdDev;
    }
    
    /**
     * Generates the next event occurrence based on current time.
     * Uses normal distribution to determine the interval.
     * 
     * @param currentTime The current simulation time
     * @param time
     * @return A new event scheduled at currentTime + normally distributed interval. Or null 
     */
    public Event generateNext(double currentTime, Aircraft aircraft, Runway runway) {
        Event event = null;
        double expected;
        
        switch (this.eventType) {      
            case LANDING:
                // normal distro around departure time, with std dev 5 minutes' (60 ticks * 5)
                expected = this.random.nextGaussian(aircraft.getScheduledTime(), 5*60);
                event = new Landing(expected, aircraft, runway.getId());
                break;
            case RUNWAY_TAKEOFF:
                // Temporarily assume that there is only one runway?
                expected = this.random.nextGaussian(aircraft.getScheduledTime(), 5*60);
                event = new RunwayTakeOff(expected, runway.getId());
                break;
            case ENTER_HP:
                expected = this.random.nextGaussian(aircraft.getScheduledTime(), 5*60);
                event = new EnterHP(expected, aircraft);
                break;
            case LEAVE_HP:
                event = new LeaveHP(currentTime, aircraft);
                break;
            // case CRITICAL_FUEL_LEVEL:
            //      handle fuel level by 
            //      break;
            case RUNWAY_OP_MODE_CHANGE:
                // Change the stored runway in place.
                event = new RunwayModeChangeEvent(currentTime, runway.getId(), runway.getMode());
                break;
            case RUNWAY_OP_STATUS_CHANGE:
                event = new RunwayStatusChangeEvent(currentTime, runway.getId(), runway.getStatus());
                break;
            case DIVERSION:
                // Wait for the element at front of holding pattern to be less than the current simulation time
                // break;
                // event = new Diversion(currentTime, aircraft);
                // Could be an event for notifying the view.
                break;
            case AIRCRAFT_EM_STATUS_CHANGE:
            //     Another event for the UI
            //     event = new Aircraft;
                break;
            case CRITICAL_FUEL_LEVEL:
                break;
            case MECHANICAL_FAILURE:
                break;
            case PASSENGER_HEALTH:
                break;
            case RUNWAY_FREE:
                break;
            case TAKEOFF_CANCELLATION:
                break;
            default:
                System.out.println("Unimplemented or error");
        }
         return event;
    }

    
    /**
     * Sets the random seed for reproducible simulations.
     */
    public void setSeed(long seed) {
        this.random = new Random(seed);
    }
}
