package com.airport_simulation.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class View extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/airport_simulation/view/resources/graphics.fxml")));
        scene.getStylesheets().add(getClass().getResource("/com/airport_simulation/view/resources/style.css").toExternalForm());
        stage.setTitle("Airport Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private SplitPane middleAndRight;

    @FXML
    private Button collapseButton;

    private boolean open = true;

    @FXML
    public void collapseButtonPressed(ActionEvent event) {
        
        double targetPosition = open ? 1.0 : 0.75;
        
        Timeline timeline = new Timeline(
            new KeyFrame(
                Duration.millis(200),
                new KeyValue(
                    middleAndRight.getDividers().get(0).positionProperty(),
                    targetPosition
                )
            )
        );
        timeline.play();

        collapseButton.getStyleClass().removeAll("open", "closed");

        if (open) {
            collapseButton.setText("▶");
            collapseButton.getStyleClass().add("closed");
        } else {
            collapseButton.setText("◀");
            collapseButton.getStyleClass().add("open");
        }

        // flip state
        open = !open;
    }

}
