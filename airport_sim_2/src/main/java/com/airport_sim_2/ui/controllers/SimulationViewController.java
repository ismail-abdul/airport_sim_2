package com.airport_sim_2.ui.controllers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Aircraft;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

/**
 * Controller for the Simulation View
 * Displays real-time simulation progress and aircraft queues
 */
public class SimulationViewController {
    
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Button stopButton;
    @FXML private Label statusIndicator;
    @FXML private Label elapsedTimeLabel;
    
    @FXML private Label totalArrivedLabel;
    @FXML private Label totalDepartedLabel;
    @FXML private Label diversionsLabel;
    @FXML private Label cancellationsLabel;
    @FXML private Label holdingQueueSizeLabel;
    @FXML private Label takeoffQueueSizeLabel;
    
    @FXML private TableView<AircraftTableRow> holdingPatternTable;
    @FXML private TableColumn<AircraftTableRow, String> hpCallsignColumn;
    @FXML private TableColumn<AircraftTableRow, String> hpOperatorColumn;
    @FXML private TableColumn<AircraftTableRow, Integer> hpAltitudeColumn;
    @FXML private TableColumn<AircraftTableRow, String> hpFuelColumn;
    @FXML private TableColumn<AircraftTableRow, String> hpStatusColumn;
    
    @FXML private TableView<AircraftTableRow> takeoffQueueTable;
    @FXML private TableColumn<AircraftTableRow, Integer> toPositionColumn;
    @FXML private TableColumn<AircraftTableRow, String> toCallsignColumn;
    @FXML private TableColumn<AircraftTableRow, String> toOperatorColumn;
    @FXML private TableColumn<AircraftTableRow, String> toFuelColumn;
    
    @FXML private TextArea simulationLog;
    
    private MainController mainController;
    private SimulationEngine engine;
    private SimulationContext context;
    private Timeline updateTimeline;
    private Thread simulationThread;
    
    private ObservableList<AircraftTableRow> holdingPatternList;
    private ObservableList<AircraftTableRow> takeoffQueueList;
    
    private IntegerProperty totalArrived = new SimpleIntegerProperty(0);
    private IntegerProperty totalDeparted = new SimpleIntegerProperty(0);
    private IntegerProperty diversions = new SimpleIntegerProperty(0);
    private IntegerProperty cancellations = new SimpleIntegerProperty(0);
    
    private boolean isRunning = false;
    private boolean isPaused = false;
    private long startTime;
    
    @FXML
    public void initialize() {
        holdingPatternList = FXCollections.observableArrayList();
        takeoffQueueList = FXCollections.observableArrayList();
        
        // Configure holding pattern table
        hpCallsignColumn.setCellValueFactory(new PropertyValueFactory<>("callsign"));
        hpOperatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        hpAltitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        hpFuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        hpStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        holdingPatternTable.setItems(holdingPatternList);
        
        // Configure takeoff queue table
        toPositionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        toCallsignColumn.setCellValueFactory(new PropertyValueFactory<>("callsign"));
        toOperatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        toFuelColumn.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        takeoffQueueTable.setItems(takeoffQueueList);
        
        // Add row factory for emergency highlighting
        holdingPatternTable.setRowFactory(tv -> new TableRow<AircraftTableRow>() {
            @Override
            protected void updateItem(AircraftTableRow item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getStatus().equals("FUEL")) {
                    setStyle("-fx-background-color: #FEE2E2;");
                } else {
                    setStyle("");
                }
            }
        });
        
        // Bind labels to properties
        totalArrivedLabel.textProperty().bind(totalArrived.asString());
        totalDepartedLabel.textProperty().bind(totalDeparted.asString());
        diversionsLabel.textProperty().bind(diversions.asString());
        cancellationsLabel.textProperty().bind(cancellations.asString());
        
        // Initial button states
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
    }
    
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    
    public void startSimulation(SimulationEngine engine, SimulationContext context) {
        this.engine = engine;
        this.context = context;
        
        resetMetrics();
        simulationLog.clear();
        
        logMessage("INFO", "Simulation initialized");
        logMessage("INFO", "Configuration: " + context.getRunways().size() + " runways");
        
        // Start the simulation in a background thread
        simulationThread = new Thread(() -> {
            try {
                isRunning = true;
                startTime = System.currentTimeMillis();
                
                Platform.runLater(() -> {
                    playButton.setDisable(true);
                    pauseButton.setDisable(false);
                    stopButton.setDisable(false);
                    updateStatusIndicator("RUNNING");
                });
                
                // Run the simulation
                engine.run();
                
                Platform.runLater(() -> {
                    isRunning = false;
                    updateStatusIndicator("COMPLETED");
                    logMessage("INFO", "Simulation completed");
                    playButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(true);
                    mainController.onSimulationComplete();
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    logMessage("ERROR", "Simulation error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
        
        simulationThread.setDaemon(true);
        simulationThread.start();
        
        // Start UI update timeline
        startUIUpdates();
    }
    
    private void startUIUpdates() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateUI();
        }));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }
    
    private void updateUI() {
        if (context == null || engine == null) return;

        // Update simulation time (not wall-clock time)
        long simSeconds = (long) engine.getCurrentTime();
        elapsedTimeLabel.setText(formatTime(simSeconds));

        // Update statistics from collector
        totalArrived.set(context.getStatistics().getArrivedCount());
        totalDeparted.set(context.getStatistics().getDepartedCount());
        diversions.set(context.getStatistics().getDivertedCount());
        cancellations.set(context.getStatistics().getCancelledCount());

        // Update queue sizes
        int hpSize = context.getHoldingPattern().size();
        int toSize = context.getTakeOffQueue().size();
        holdingQueueSizeLabel.setText(String.valueOf(hpSize));
        takeoffQueueSizeLabel.setText(String.valueOf(toSize));
        
        // Update holding pattern table
        updateHoldingPatternTable();
        
        // Update takeoff queue table
        updateTakeoffQueueTable();
    }
    
    private void updateHoldingPatternTable() {
        holdingPatternList.clear();
        int position = 1;
        
        for (Aircraft aircraft : context.getHoldingPattern().getQueue()) {
            AircraftTableRow row = new AircraftTableRow(
                position++,
                aircraft.getCallsign(),
                aircraft.getOperator(),
                aircraft.getAltitude(),
                formatFuel(aircraft.getFuel()),
                aircraft.getStatus().toString()
            );
            holdingPatternList.add(row);
        }
    }
    
    private void updateTakeoffQueueTable() {
        takeoffQueueList.clear();
        int position = 1;
        
        for (Aircraft aircraft : context.getTakeOffQueue().getQueue()) {
            AircraftTableRow row = new AircraftTableRow(
                position++,
                aircraft.getCallsign(),
                aircraft.getOperator(),
                aircraft.getAltitude(),
                formatFuel(aircraft.getFuel()),
                aircraft.getStatus().toString()
            );
            takeoffQueueList.add(row);
        }
    }
    
    @FXML
    private void handlePlay() {
        if (isPaused) {
            isPaused = false;
            if (engine != null) engine.setPaused(false);
            playButton.setDisable(true);
            pauseButton.setDisable(false);
            updateStatusIndicator("RUNNING");
            logMessage("INFO", "Simulation resumed");
        }
    }
    
    @FXML
    private void handlePause() {
        isPaused = true;
        if (engine != null) engine.setPaused(true);
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        updateStatusIndicator("PAUSED");
        logMessage("INFO", "Simulation paused");
    }
    
    @FXML
    private void handleStop() {
        if (engine != null) engine.setStop();
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        isRunning = false;
        updateStatusIndicator("STOPPED");
        logMessage("WARNING", "Simulation stopped by user");
        
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
    }
    
    private void updateStatusIndicator(String status) {
        statusIndicator.setText(status);
        statusIndicator.getStyleClass().removeAll("status-running", "status-paused", "status-completed", "status-stopped");
        
        switch (status) {
            case "RUNNING":
                statusIndicator.getStyleClass().add("status-running");
                break;
            case "PAUSED":
                statusIndicator.getStyleClass().add("status-paused");
                break;
            case "COMPLETED":
                statusIndicator.getStyleClass().add("status-completed");
                break;
            case "STOPPED":
                statusIndicator.getStyleClass().add("status-stopped");
                break;
        }
    }
    
    public void logMessage(String level, String message) {
        Platform.runLater(() -> {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String logEntry = String.format("[%s] %s: %s\n", timestamp, level, message);
            simulationLog.appendText(logEntry);
        });
    }
    
    private void resetMetrics() {
        totalArrived.set(0);
        totalDeparted.set(0);
        diversions.set(0);
        cancellations.set(0);
        elapsedTimeLabel.setText("00:00:00");
        holdingQueueSizeLabel.setText("0");
        takeoffQueueSizeLabel.setText("0");
    }
    
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    
    private String formatFuel(float fuel) {
        return String.format("%.1f L", fuel);
    }
    
    /**
     * Table row class for aircraft display
     */
    public static class AircraftTableRow {
        private final IntegerProperty position;
        private final StringProperty callsign;
        private final StringProperty operator;
        private final IntegerProperty altitude;
        private final StringProperty fuel;
        private final StringProperty status;
        
        public AircraftTableRow(int position, String callsign, String operator,
                               int altitude, String fuel, String status) {
            this.position = new SimpleIntegerProperty(position);
            this.callsign = new SimpleStringProperty(callsign);
            this.operator = new SimpleStringProperty(operator);
            this.altitude = new SimpleIntegerProperty(altitude);
            this.fuel = new SimpleStringProperty(fuel);
            this.status = new SimpleStringProperty(status);
        }
        
        // Property getters
        public IntegerProperty positionProperty() { return position; }
        public StringProperty callsignProperty() { return callsign; }
        public StringProperty operatorProperty() { return operator; }
        public IntegerProperty altitudeProperty() { return altitude; }
        public StringProperty fuelProperty() { return fuel; }
        public StringProperty statusProperty() { return status; }
        
        // Value getters
        public int getPosition() { return position.get(); }
        public String getCallsign() { return callsign.get(); }
        public String getOperator() { return operator.get(); }
        public int getAltitude() { return altitude.get(); }
        public String getFuel() { return fuel.get(); }
        public String getStatus() { return status.get(); }
    }
}
