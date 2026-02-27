package com.airport_sim_2.events;
import com.airport_sim_2.objects.*;


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

    // get the runway associated with this landing 
    public Runway getRunway() {
        return runway;
    }
    public void setRunway(Runway runway) {
        this.runway = runway;
    }

}
