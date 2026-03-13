package com.airport_sim_2.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;
import com.airport_sim_2.ui.controllers.MainController.RunwayConfig;
import com.airport_sim_2.ui.controllers.MainController.SimulationConfig;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the Configuration View
 * Handles runway configuration and simulation parameters
 */
public class ConfigurationController {
    
    @FXML private TableView<RunwayTableRow> runwayTable;
    @FXML private TableColumn<RunwayTableRow, String> numberColumn;
    @FXML private TableColumn<RunwayTableRow, RunwayOpMode> modeColumn;
    @FXML private TableColumn<RunwayTableRow, RunwayOperationalStatus> statusColumn;
    @FXML private TableColumn<RunwayTableRow, Integer> lengthColumn;
    @FXML private TableColumn<RunwayTableRow, Integer> bearingColumn;
    
    @FXML private Button addRunwayButton;
    @FXML private Button removeRunwayButton;
    
    @FXML private TextField inboundRateField;
    @FXML private TextField outboundRateField;
    @FXML private TextField durationField;
    
    @FXML private Button startSimulationButton;
    @FXML private Label validationLabel;
    
    private MainController mainController;
    private ObservableList<RunwayTableRow> runways;
    private int runwayCounter = 1;
    
    @FXML
    public void initialize() {
        runways = FXCollections.observableArrayList();
        
        // Configure table columns
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("mode"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        bearingColumn.setCellValueFactory(new PropertyValueFactory<>("bearing"));
        
        // Make mode and status columns editable with combo boxes
        modeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(RunwayOpMode.values()));
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(RunwayOperationalStatus.values()));
        
        runwayTable.setItems(runways);
        runwayTable.setEditable(true);
        
        // Add initial runway
        addDefaultRunway();
        
        // Set default values for simulation parameters
        inboundRateField.setText("15");
        outboundRateField.setText("15");
        durationField.setText("120");
        
        // Add text field validation
        addNumericValidation(inboundRateField);
        addNumericValidation(outboundRateField);
        addNumericValidation(durationField);
    }
    
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    
    @FXML
    private void handleAddRunway() {
        addDefaultRunway();
    }
    
    @FXML
    private void handleRemoveRunway() {
        RunwayTableRow selected = runwayTable.getSelectionModel().getSelectedItem();
        if (selected != null && runways.size() > 1) {
            runways.remove(selected);
        } else if (runways.size() <= 1) {
            showWarning("At least one runway is required");
        } else {
            showWarning("Please select a runway to remove");
        }
    }
    
    @FXML
    private void handleStartSimulation() {
        if (validateConfiguration()) {
            SimulationConfig config = createSimulationConfig();
            mainController.startSimulation(config);
        }
    }
    
    private void addDefaultRunway() {
        String runwayNumber = String.format("%02dL", runwayCounter);
        RunwayTableRow runway = new RunwayTableRow(
            "RWY" + runwayCounter,
            runwayNumber,
            RunwayOpMode.MIXED_MODE,
            RunwayOperationalStatus.AVAILABLE,
            3000,
            (runwayCounter * 90) % 360
        );
        runways.add(runway);
        runwayCounter++;
    }
    
    private boolean validateConfiguration() {
        validationLabel.setText("");
        
        // Check we have at least one runway
        if (runways.isEmpty()) {
            validationLabel.setText("At least one runway is required");
            return false;
        }
        
        // Validate traffic rates
        try {
            int inbound = Integer.parseInt(inboundRateField.getText());
            int outbound = Integer.parseInt(outboundRateField.getText());
            int duration = Integer.parseInt(durationField.getText());
            
            if (inbound < 0 || outbound < 0) {
                validationLabel.setText("Traffic rates must be non-negative");
                return false;
            }
            
            if (duration <= 0) {
                validationLabel.setText("Duration must be greater than 0");
                return false;
            }
            
        } catch (NumberFormatException e) {
            validationLabel.setText("Please enter valid numbers for all parameters");
            return false;
        }
        
        return true;
    }
    
    private SimulationConfig createSimulationConfig() {
        List<RunwayConfig> runwayConfigs = new ArrayList<>();
        
        for (RunwayTableRow row : runways) {
            RunwayConfig config = new RunwayConfig(
                row.getId(),
                row.getNumber(),
                row.getMode(),
                row.getStatus(),
                row.getLength(),
                row.getBearing()
            );
            runwayConfigs.add(config);
        }
        
        int inbound = Integer.parseInt(inboundRateField.getText());
        int outbound = Integer.parseInt(outboundRateField.getText());
        int duration = Integer.parseInt(durationField.getText());
        
        return new SimulationConfig(runwayConfigs, inbound, outbound, duration);
    }
    
    private void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    public void reset() {
        runways.clear();
        runwayCounter = 1;
        addDefaultRunway();
        inboundRateField.setText("15");
        outboundRateField.setText("15");
        durationField.setText("120");
        validationLabel.setText("");
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Table row class for runway display
     */
    public static class RunwayTableRow {
        private final StringProperty id;
        private final StringProperty number;
        private final ObjectProperty<RunwayOpMode> mode;
        private final ObjectProperty<RunwayOperationalStatus> status;
        private final IntegerProperty length;
        private final IntegerProperty bearing;
        
        public RunwayTableRow(String id, String number, RunwayOpMode mode,
                            RunwayOperationalStatus status, int length, int bearing) {
            this.id = new SimpleStringProperty(id);
            this.number = new SimpleStringProperty(number);
            this.mode = new SimpleObjectProperty<>(mode);
            this.status = new SimpleObjectProperty<>(status);
            this.length = new SimpleIntegerProperty(length);
            this.bearing = new SimpleIntegerProperty(bearing);
        }
        
        // Property getters
        public StringProperty idProperty() { return id; }
        public StringProperty numberProperty() { return number; }
        public ObjectProperty<RunwayOpMode> modeProperty() { return mode; }
        public ObjectProperty<RunwayOperationalStatus> statusProperty() { return status; }
        public IntegerProperty lengthProperty() { return length; }
        public IntegerProperty bearingProperty() { return bearing; }
        
        // Value getters
        public String getId() { return id.get(); }
        public String getNumber() { return number.get(); }
        public RunwayOpMode getMode() { return mode.get(); }
        public RunwayOperationalStatus getStatus() { return status.get(); }
        public int getLength() { return length.get(); }
        public int getBearing() { return bearing.get(); }
        
        // Setters
        public void setMode(RunwayOpMode mode) { this.mode.set(mode); }
        public void setStatus(RunwayOperationalStatus status) { this.status.set(status); }
    }
}
