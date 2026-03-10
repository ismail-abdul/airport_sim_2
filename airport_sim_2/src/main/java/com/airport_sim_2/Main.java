package com.airport_sim_2;

import com.airport_sim_2.controller.SimulationController;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        double endtime = 60 * (10 * 10 * 10);
        SimulationController controller = new SimulationController(endtime);
        controller.getEngine().run();
    }
    
}