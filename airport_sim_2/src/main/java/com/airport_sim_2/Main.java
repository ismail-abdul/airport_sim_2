package com.airport_sim_2;

import com.airport_sim_2.controller.SimulationController;

public class Main {

    public static void main(String[] args) {
        double endtime = 60 * (10 * 10 * 10);
        SimulationController controller = new SimulationController(endtime);
        controller.startSimulation();
        System.out.println(controller.getStatistics());
    }
}
