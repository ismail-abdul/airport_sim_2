package com.airport_sim_2.events;
import java.time.LocalDateTime;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.objects.Aircraft;
import com.airport_sim_2.objects.Runway;

/* What information does the Landing Class need?
Keep in mind seperation of concerns from the controller. 

Aircraft reference
Runway reference
timestamp etc
*/

public class Landing extends AbstractAircraftEvent   {
    private Runway runway;
    
    public Landing(Aircraft aircraft , Runway runway, LocalDateTime ts) {
        super(aircraft, ts);
        this.runway = runway;
    }

    public Landing(Aircraft aircraft, LocalDateTime ts) {
        super(aircraft, ts);
        this.runway = null;
    }

    // get the runway associated with this landing 
    public Runway getRunway() {
        return runway;
    }
    public void setRunway(Runway runway) {
        this.runway = runway;
    }

    @Override
    public void process(SimulationContext ctx) {

    }

    @Override
    public int compareTo(Event other) {
        return this.getTime().compareTo(other.getTime());
    }

    @Override
    public LocalDateTime getTime(){
        return super.getTime();
    }
}
