package com.airport_sim_2.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Main controller that manages navigation between the three main views:
 * - Configuration View
 * - Simulation View  
 * - Results View
 */
public class MainController {
    
    @FXML private TabPane mainTabPane;
    @FXML private Tab configTab;
    @FXML private Tab simulationTab;
    @FXML private Tab resultsTab;
    @FXML private Label statusLabel;
    
    private ConfigurationController configController;
    private SimulationViewController simulationController;
    private ResultsController resultsController;
    
    private SimulationContext simulationContext;
    private SimulationEngine simulationEngine;
    
    @FXML
    public void initialize() {
        // Disable simulation and results tabs initially
        simulationTab.setDisable(true);
        resultsTab.setDisable(true);
        
        loadConfigurationView();
        loadSimulationView();
        loadResultsView();
        
        // Update status
        updateStatus("Ready to configure simulation");
    }
    
    private void loadConfigurationView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/configuration-view.fxml")
            );
            BorderPane configView = loader.load();
            configController = loader.getController();
            configController.setMainController(this);
            configTab.setContent(configView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load configuration view");
        }
    }
    
    private void loadSimulationView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/simulation-view.fxml")
            );
            BorderPane simView = loader.load();
            simulationController = loader.getController();
            simulationController.setMainController(this);
            simulationTab.setContent(simView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load simulation view");
        }
    }
    
    private void loadResultsView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/results-view.fxml")
            );
            BorderPane resultsView = loader.load();
            resultsController = loader.getController();
            resultsController.setMainController(this);
            resultsTab.setContent(resultsView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load results view");
        }
    }
    
    /**
     * Called when user starts the simulation from configuration view
     */
    public void startSimulation(SimulationConfig config) {
        // Create simulation context from config
        simulationContext = createSimulationContext(config);
        
        // Create simulation engine
        simulationEngine = new SimulationEngine(
            config.getDuration() * 60.0, // Convert minutes to seconds
            simulationContext
        );
        
        // Enable simulation tab and switch to it
        simulationTab.setDisable(false);
        mainTabPane.getSelectionModel().select(simulationTab);
        
        // Start the simulation
        simulationController.startSimulation(simulationEngine, simulationContext);
        
        updateStatus("Simulation running...");
    }
    
    /**
     * Called when simulation completes
     */
    public void onSimulationComplete() {
        // Enable results tab and switch to it
        resultsTab.setDisable(false);
        mainTabPane.getSelectionModel().select(resultsTab);
        
        // Load results
        resultsController.loadResults(simulationEngine, simulationContext);
        
        updateStatus("Simulation completed");
    }
    
    /**
     * Reset the application to start a new simulation
     */
    public void resetSimulation() {
        simulationTab.setDisable(true);
        resultsTab.setDisable(true);
        mainTabPane.getSelectionModel().select(configTab);
        configController.reset();
        updateStatus("Ready to configure new simulation");
    }
    
    private SimulationContext createSimulationContext(SimulationConfig config) {
        List<Runway> runways = new ArrayList<>();
        int runwayId = 1;

        for (RunwayConfig rc : config.getRunways()) {
            Runway runway = new Runway(
                runwayId++,
                rc.getMode(),
                rc.getStatus()
            );
            runways.add(runway);
        }

        return new SimulationContext(new HoldingPattern(), new TakeOffQueue(), runways, new StatisticsCollector());
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Configuration data class
     */
    public static class SimulationConfig {
        private List<RunwayConfig> runways;
        private int inboundRate;
        private int outboundRate;
        private int duration;
        
        public SimulationConfig(List<RunwayConfig> runways, int inboundRate, 
                              int outboundRate, int duration) {
            this.runways = runways;
            this.inboundRate = inboundRate;
            this.outboundRate = outboundRate;
            this.duration = duration;
        }
        
        // Getters
        public List<RunwayConfig> getRunways() { return runways; }
        public int getInboundRate() { return inboundRate; }
        public int getOutboundRate() { return outboundRate; }
        public int getDuration() { return duration; }
    }
    
    /**
     * Runway configuration data class
     */
    public static class RunwayConfig {
        private String id;
        private String number;
        private RunwayOpMode mode;
        private RunwayOperationalStatus status;
        private int length;
        private int bearing;
        
        public RunwayConfig(String id, String number, RunwayOpMode mode,
                          RunwayOperationalStatus status, int length, int bearing) {
            this.id = id;
            this.number = number;
            this.mode = mode;
            this.status = status;
            this.length = length;
            this.bearing = bearing;
        }
        
        // Getters
        public String getId() { return id; }
        public String getNumber() { return number; }
        public RunwayOpMode getMode() { return mode; }
        public RunwayOperationalStatus getStatus() { return status; }
        public int getLength() { return length; }
        public int getBearing() { return bearing; }
        
        // Setters
        public void setMode(RunwayOpMode mode) { this.mode = mode; }
        public void setStatus(RunwayOperationalStatus status) { this.status = status; }
    }
}
