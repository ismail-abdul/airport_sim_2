package com.airport_sim_2.model;

import java.time.LocalDateTime;
import java.util.Random;

import com.airport_sim_2.events.EnterHP;
import com.airport_sim_2.events.Event;
import com.airport_sim_2.events.Landing;
import com.airport_sim_2.events.TakeOffEvent;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;


/**
 * Generate a specific type of event that is statistically independent from the generation of other events.
 */
public class EventGenerator {
    private Random random;
    private final EventType eventType;
    
    /**
     * @param eventType The type of event this generator creates
     * @param mean Mean time interval between events (in your time units)
     * @param stdDev Standard deviation of the interval
     */
    public EventGenerator(EventType eventType, double mean, double stdDev) {
        this.eventType = eventType;
        this.random = new Random();
    }
    
    /**
     * Generates the next event occurrence based on current time.
     * Uses normal distribution to determine the interval.
     * 
     * @param currentTime The current simulation time
     * @param time
     * @return A new event scheduled at currentTime + normally distributed interval. Or null 
     */
    public Event generateNext(double currentTime, Aircraft aircraft, Runway runway, LocalDateTime time) {
        Event event = null;
        double expected;
        
        switch (this.eventType) {      
            case LANDING:
                // normal distro around departure time, with std dev 5 minutes' (60 ticks * 5)
                expected = this.random.nextGaussian(aircraft.getScheduledTime(), 5*60);
                event = new Landing(expected, aircraft, runway.getId());
                break;
            case TAKEOFF:
                // Temporarily assume that there is only one runway?
                expected = this.random.nextGaussian(aircraft.getScheduledTime(), 5*60);
                event = new TakeOffEvent(expected, runway.getId());
                break;
            case ENTER_HP:
                expected = this.random.nextGaussian(aircraft.getScheduledTime(), 5*60);
                event = new EnterHP(expected, aircraft);
                break;
            /** 
            case DIVERSION:
                Schedule the diversion in the future in case 
                Or wait for the element at front of holding pattern to be less than the current simulation time
                break;
            */
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
