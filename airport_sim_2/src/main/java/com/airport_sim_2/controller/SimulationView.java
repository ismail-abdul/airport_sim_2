package com.airport_sim_2.controller;
import java.util.List;

import com.airport_sim_2.objects.Runway;

public class SimulationView {
    private SimulationController controller;

    public SimulationView(SimulationController controller){
        this.controller = controller;
    }

    public void DisplayUI(){
        displayTakeOffQueue();
        displayHoldingPattern();
        displayRunways();
        displayTime();
    }

    private void displayTakeOffQueue(){

        // Gets all the aircrafts' callsign from the take-off queue
        String[] callsigns = controller.getTakeOffQueueCallsigns();
    
    }

    private void displayHoldingPattern(){

        // Gets all the aircrafts' callsign from the holding pattern
        String[] callsigns = controller.getHoldingPatternCallsigns();
    
    }

    private void displayRunways(){

        // Gets all the runways in the simulation
        List<Runway> runways = controller.getRunways();
    
    }

    private void displayTime(){

        // Gets the current time, which is the latest event
        double current_time = controller.getCurrentTime();
    
    }

}
