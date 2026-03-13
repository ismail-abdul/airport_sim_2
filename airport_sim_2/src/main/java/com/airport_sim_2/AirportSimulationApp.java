package com.airport_sim_2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main JavaFX Application for Airport Simulation System
 * 
 * This application provides a visual interface for:
 * - Configuring simulation parameters and runways
 * - Running and monitoring airport simulations in real-time
 * - Analyzing results with charts and statistics
 */
public class AirportSimulationApp extends Application {
    
    private static final String APP_TITLE = "Airport Simulation System";
    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 800;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main layout
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main-layout.fxml")
            );
            BorderPane root = loader.load();
            
            // Create the scene
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            
            // Load CSS stylesheet
            scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
            );
            
            // Configure the stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            
            // Show the application
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Failed to start application", e.getMessage());
        }
    }
    
    private void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
