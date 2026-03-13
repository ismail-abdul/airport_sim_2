package com.airport_sim_2.ui.controllers;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.controller.TimeSeriesPoint;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Controller for the Results View
 * Displays comprehensive analysis after simulation completes
 */
public class ResultsController {
    
    @FXML private Label totalAircraftLabel;
    @FXML private Label avgWaitTimeLabel;
    @FXML private Label maxQueueLengthLabel;
    @FXML private Label diversionsCountLabel;
    @FXML private Label cancellationsCountLabel;
    @FXML private Label runwayUtilizationLabel;
    
    @FXML private LineChart<Number, Number> queueLengthChart;
    @FXML private BarChart<String, Number> delayDistributionChart;
    @FXML private LineChart<Number, Number> throughputChart;
    
    @FXML private TableView<RunwayPerformanceRow> runwayPerformanceTable;
    @FXML private TableColumn<RunwayPerformanceRow, String> runwayNumberColumn;
    @FXML private TableColumn<RunwayPerformanceRow, String> runwayModeColumn;
    @FXML private TableColumn<RunwayPerformanceRow, Integer> totalOpsColumn;
    @FXML private TableColumn<RunwayPerformanceRow, String> utilizationColumn;
    
    @FXML private TextArea detailedReportArea;
    @FXML private Button exportPdfButton;
    @FXML private Button exportCsvButton;
    @FXML private Button newSimulationButton;
    
    private MainController mainController;
    private SimulationEngine engine;
    private SimulationContext context;
    
    @FXML
    public void initialize() {
        // Configure runway performance table
        runwayNumberColumn.setCellValueFactory(cellData -> cellData.getValue().runwayNumber);
        runwayModeColumn.setCellValueFactory(cellData -> cellData.getValue().mode);
        totalOpsColumn.setCellValueFactory(cellData -> cellData.getValue().totalOperations.asObject());
        utilizationColumn.setCellValueFactory(cellData -> cellData.getValue().utilization);
    }
    
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    
    public void loadResults(SimulationEngine engine, SimulationContext context) {
        this.engine = engine;
        this.context = context;
        
        calculateAndDisplayMetrics();
        generateCharts();
        populateRunwayPerformanceTable();
        generateDetailedReport();
    }
    
    private void calculateAndDisplayMetrics() {
        StatisticsCollector stats = context.getStatistics();

        int total = stats.getArrivedCount() + stats.getDepartedCount();
        totalAircraftLabel.setText(String.valueOf(total));

        int arrCount = stats.getArrivalWaitTimes().size();
        int depCount = stats.getDepartureWaitTimes().size();
        double avgWait = 0;
        if (arrCount + depCount > 0) {
            avgWait = (stats.getAverageArrivalWait() * arrCount + stats.getAverageDepartureWait() * depCount)
                      / (arrCount + depCount);
        }
        avgWaitTimeLabel.setText(String.format("%.1f min", avgWait));

        int maxQueue = Math.max(stats.getMaxHoldingSize(), stats.getMaxTakeoffQueueSize());
        maxQueueLengthLabel.setText(String.valueOf(maxQueue));

        diversionsCountLabel.setText(String.valueOf(stats.getDivertedCount()));
        cancellationsCountLabel.setText(String.valueOf(stats.getCancelledCount()));

        double totalSimMinutes = engine.getEndTime() / 60.0;
        int numRunways = context.getRunways() != null ? context.getRunways().size() : 1;
        if (totalSimMinutes > 0 && numRunways > 0) {
            double occupiedMinutes = stats.getArrivedCount() * context.getLandingDuration()
                                   + stats.getDepartedCount() * context.getTakeOffDuration();
            double utilization = occupiedMinutes / (totalSimMinutes * numRunways) * 100.0;
            runwayUtilizationLabel.setText(String.format("%.1f%%", Math.min(utilization, 100.0)));
        } else {
            runwayUtilizationLabel.setText("N/A");
        }
    }
    
    private void generateCharts() {
        generateQueueLengthChart();
        generateDelayDistributionChart();
        generateThroughputChart();
    }
    
    private void generateQueueLengthChart() {
        queueLengthChart.getData().clear();

        StatisticsCollector stats = context.getStatistics();

        XYChart.Series<Number, Number> holdingSeries = new XYChart.Series<>();
        holdingSeries.setName("Holding Pattern");
        for (TimeSeriesPoint p : stats.getHp_time_series()) {
            holdingSeries.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));
        }

        XYChart.Series<Number, Number> takeoffSeries = new XYChart.Series<>();
        takeoffSeries.setName("Takeoff Queue");
        for (TimeSeriesPoint p : stats.getToq_time_series()) {
            takeoffSeries.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));
        }

        queueLengthChart.getData().addAll(holdingSeries, takeoffSeries);
    }
    
    private void generateDelayDistributionChart() {
        delayDistributionChart.getData().clear();

        StatisticsCollector stats = context.getStatistics();

        // Collect all wait times (arrivals + departures)
        List<Double> allWaits = new ArrayList<>();
        allWaits.addAll(stats.getArrivalWaitTimes());
        allWaits.addAll(stats.getDepartureWaitTimes());

        if (allWaits.isEmpty()) return;

        // Compute mean and standard deviation
        double mean = allWaits.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = allWaits.stream().mapToDouble(w -> (w - mean) * (w - mean)).average().orElse(0);
        double stdDev = Math.sqrt(variance);

        // Dynamic bin range: cover all data, centred on mean, using 8 bins
        int numBins = 8;
        double dataMin = allWaits.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double dataMax = allWaits.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double sigma   = Math.max(stdDev, (dataMax - dataMin) / numBins);
        double rangeMin = Math.max(0, Math.min(dataMin, mean - 3 * sigma));
        double rangeMax = Math.max(dataMax, mean + 3 * sigma);
        double binWidth = (rangeMax - rangeMin) / numBins;
        if (binWidth <= 0) binWidth = 1;

        // Fill bins (last bin absorbs all extremes)
        int[] counts = new int[numBins];
        for (double w : allWaits) {
            int idx = w < rangeMin ? 0
                    : w >= rangeMax ? numBins - 1
                    : Math.min((int)((w - rangeMin) / binWidth), numBins - 1);
            counts[idx]++;
        }

        // Populate BarChart with dynamic labels
        XYChart.Series<String, Number> histSeries = new XYChart.Series<>();
        histSeries.setName("Wait Time Distribution");
        for (int i = 0; i < numBins; i++) {
            double lo = rangeMin + i * binWidth;
            double hi = lo + binWidth;
            String label = (i == numBins - 1)
                    ? String.format("%.1f+", lo)
                    : String.format("%.1f-%.1f", lo, hi);
            histSeries.getData().add(new XYChart.Data<>(label, counts[i]));
        }
        delayDistributionChart.getData().add(histSeries);

        // Fix y-axis upper bound so the normal curve and bars share the same scale
        int maxCount = Arrays.stream(counts).max().orElse(1);
        NumberAxis yAxis = (NumberAxis) delayDistributionChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxCount * 1.25);
        yAxis.setTickUnit(Math.max(1, Math.round(maxCount / 5.0)));

        // Overlay normal distribution curve directly onto the chart's plot area
        if (stdDev > 0) {
            final double fMean     = mean;
            final double fStdDev   = stdDev;
            final double fRangeMin = rangeMin;
            final double fBinWidth = binWidth;
            final int    fNumBins  = numBins;
            final double fScale    = allWaits.size() * binWidth; // density → counts
            final double fUpperBound = maxCount * 1.25;

            Platform.runLater(() -> {
                Node plotBg = delayDistributionChart.lookup(".chart-plot-background");
                if (plotBg == null || !(plotBg.getParent() instanceof Pane)) return;

                Pane plotArea = (Pane) plotBg.getParent();
                // Remove any normal-curve path from a previous simulation run
                plotArea.getChildren().removeIf(
                        n -> n instanceof Path && "normalCurve".equals(n.getUserData()));

                double plotW = plotBg.getBoundsInParent().getWidth();
                double plotH = plotBg.getBoundsInParent().getHeight();
                double plotX0 = plotBg.getBoundsInParent().getMinX();
                double plotY0 = plotBg.getBoundsInParent().getMinY();
                if (plotW <= 0 || plotH <= 0) return;

                Path path = new Path();
                path.setUserData("normalCurve");
                path.setStroke(Color.RED);
                path.setStrokeWidth(2.5);
                path.getStrokeDashArray().addAll(8.0, 5.0);
                path.setFill(Color.TRANSPARENT);
                path.setMouseTransparent(true);

                // chartX ∈ [0, numBins] maps linearly to plotW pixels.
                // Category i centre = (i + 0.5)/numBins * plotW, matching JavaFX CategoryAxis.
                boolean first = true;
                for (int p = 0; p <= 120; p++) {
                    double chartX = fNumBins * p / 120.0;
                    double dataX  = fRangeMin + chartX * fBinWidth;
                    double z      = (dataX - fMean) / fStdDev;
                    double yVal   = fScale * Math.exp(-0.5 * z * z)
                                    / (fStdDev * Math.sqrt(2 * Math.PI));

                    double px = plotX0 + (chartX / fNumBins) * plotW;
                    double py = plotY0 + plotH - (yVal / fUpperBound) * plotH;
                    py = Math.max(plotY0, Math.min(plotY0 + plotH, py));

                    if (first) { path.getElements().add(new MoveTo(px, py)); first = false; }
                    else        { path.getElements().add(new LineTo(px, py)); }
                }

                plotArea.getChildren().add(path);

                // Add a custom legend entry for the normal curve
                Node legendNode = delayDistributionChart.lookup(".chart-legend");
                if (legendNode instanceof Pane) {
                    Pane legendPane = (Pane) legendNode;
                    legendPane.getChildren().removeIf(
                            n -> n instanceof Label && "normalCurveLegend".equals(n.getUserData()));
                    javafx.scene.shape.Line legendLine = new javafx.scene.shape.Line(0, 0, 20, 0);
                    legendLine.setStroke(Color.RED);
                    legendLine.setStrokeWidth(2.5);
                    legendLine.getStrokeDashArray().addAll(8.0, 5.0);
                    Label legendItem = new Label("Normal Distribution", legendLine);
                    legendItem.setUserData("normalCurveLegend");
                    legendItem.getStyleClass().add("chart-legend-item");
                    legendPane.getChildren().add(legendItem);
                }
            });
        }
    }
    
    private void generateThroughputChart() {
        throughputChart.getData().clear();

        StatisticsCollector stats = context.getStatistics();

        XYChart.Series<Number, Number> arrivalsSeries = new XYChart.Series<>();
        arrivalsSeries.setName("Cumulative Arrivals");
        for (TimeSeriesPoint p : stats.getArrival_time_series()) {
            arrivalsSeries.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));
        }

        XYChart.Series<Number, Number> departuresSeries = new XYChart.Series<>();
        departuresSeries.setName("Cumulative Departures");
        for (TimeSeriesPoint p : stats.getDeparture_time_series()) {
            departuresSeries.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));
        }

        throughputChart.getData().addAll(arrivalsSeries, departuresSeries);
    }
    
    private void populateRunwayPerformanceTable() {
        runwayPerformanceTable.getItems().clear();

        if (context != null && context.getRunways() != null) {
            double totalSimMinutes = engine.getEndTime() / 60.0;
            double avgOpDuration = (context.getLandingDuration() + context.getTakeOffDuration()) / 2.0;

            for (Runway runway : context.getRunways()) {
                int ops = runway.getOperationCount();
                double utilization = totalSimMinutes > 0
                    ? Math.min(ops * avgOpDuration / totalSimMinutes * 100.0, 100.0)
                    : 0.0;
                RunwayPerformanceRow row = new RunwayPerformanceRow(
                    Integer.toString(runway.getId()),
                    runway.getMode().toString(),
                    ops,
                    String.format("%.1f%%", utilization)
                );
                runwayPerformanceTable.getItems().add(row);
            }
        }
    }
    
    private void generateDetailedReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("AIRPORT SIMULATION - DETAILED REPORT\n");
        report.append("=====================================\n\n");
        
        report.append("EXECUTIVE SUMMARY\n");
        report.append("-----------------\n");
        report.append("Simulation completed successfully.\n");
        report.append("Total aircraft processed: ").append(totalAircraftLabel.getText()).append("\n");
        report.append("Average wait time: ").append(avgWaitTimeLabel.getText()).append("\n");
        report.append("Max queue length: ").append(maxQueueLengthLabel.getText()).append("\n\n");
        
        report.append("RUNWAY CONFIGURATION\n");
        report.append("--------------------\n");
        if (context != null && context.getRunways() != null) {
            for (Runway runway : context.getRunways()) {
                report.append("Runway ").append(runway.getId())
                      .append(" - Mode: ").append(runway.getMode())
                      .append(" - Status: ").append(runway.getStatus()).append("\n");
            }
        }
        
        report.append("\nKEY FINDINGS\n");
        report.append("------------\n");
        report.append("• Runway utilization was ").append(runwayUtilizationLabel.getText()).append("\n");
        report.append("• ").append(diversionsCountLabel.getText()).append(" diversions occurred\n");
        report.append("• ").append(cancellationsCountLabel.getText()).append(" cancellations occurred\n");
        
        report.append("\nCHART DESCRIPTIONS\n");
        report.append("------------------\n");
        report.append("Queue Length Over Time:\n");
        report.append("  Shows the number of aircraft waiting in the holding pattern (incoming aircraft\n");
        report.append("  circling before landing) and in the takeoff queue (departing aircraft waiting\n");
        report.append("  for runway clearance) at each point during the simulation. Sustained high values\n");
        report.append("  in either line indicate a bottleneck that may require additional runway capacity.\n\n");
        report.append("Delay Distribution:\n");
        report.append("  Histogram of wait times across all aircraft (arrivals and departures combined),\n");
        report.append("  grouped into equal-width bins. The bars show how many aircraft experienced each\n");
        report.append("  level of delay. The dashed red curve overlays a fitted normal distribution to\n");
        report.append("  highlight whether delays follow a predictable pattern or contain outliers.\n\n");
        report.append("Cumulative Throughput:\n");
        report.append("  Running totals of aircraft that have successfully landed (arrivals) and\n");
        report.append("  taken off (departures) over the course of the simulation. The steeper the\n");
        report.append("  slope, the higher the throughput rate. A persistent gap between the two\n");
        report.append("  lines signals an imbalance between inbound and outbound traffic flow.\n");

        report.append("\nRECOMMENDATIONS\n");
        report.append("---------------\n");
        report.append("• Consider adjusting traffic flow rates based on observed bottlenecks\n");
        report.append("• Monitor emergency fuel situations more closely\n");
        report.append("• Evaluate runway mode configurations for optimal throughput\n");
        
        detailedReportArea.setText(report.toString());
    }
    
    @FXML
    private void handleExportPdf() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Simulation Report");
        chooser.setInitialFileName("simulation-report.pdf");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File dest = chooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (dest == null) return; // user cancelled

        try {
            PdfExporter exporter = new PdfExporter(
                    engine, context,
                    queueLengthChart, delayDistributionChart, throughputChart);
            exporter.export(dest);
            showInfo("Report saved to:\n" + dest.getAbsolutePath());
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Export Failed");
            err.setHeaderText("Could not save PDF");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }
    
    @FXML
    private void handleExportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Simulation Data");
        chooser.setInitialFileName("simulation-data.csv");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File dest = chooser.showSaveDialog(exportCsvButton.getScene().getWindow());
        if (dest == null) return;

        try {
            new CsvExporter(engine, context).export(dest);
            showInfo("Data saved to:\n" + dest.getAbsolutePath());
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Export Failed");
            err.setHeaderText("Could not save CSV");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }
    
    @FXML
    private void handleNewSimulation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("New Simulation");
        alert.setHeaderText("Start a new simulation?");
        alert.setContentText("This will reset all current data.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                mainController.resetSimulation();
            }
        });
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Table row class for runway performance display
     */
    public static class RunwayPerformanceRow {
        private final javafx.beans.property.SimpleStringProperty runwayNumber;
        private final javafx.beans.property.SimpleStringProperty mode;
        private final javafx.beans.property.SimpleIntegerProperty totalOperations;
        private final javafx.beans.property.SimpleStringProperty utilization;
        
        public RunwayPerformanceRow(String runwayNumber, String mode, int totalOps, String utilization) {
            this.runwayNumber = new javafx.beans.property.SimpleStringProperty(runwayNumber);
            this.mode = new javafx.beans.property.SimpleStringProperty(mode);
            this.totalOperations = new javafx.beans.property.SimpleIntegerProperty(totalOps);
            this.utilization = new javafx.beans.property.SimpleStringProperty(utilization);
        }
        
        public String getRunwayNumber() { return runwayNumber.get(); }
        public String getMode() { return mode.get(); }
        public int getTotalOperations() { return totalOperations.get(); }
        public String getUtilization() { return utilization.get(); }
    }
}
