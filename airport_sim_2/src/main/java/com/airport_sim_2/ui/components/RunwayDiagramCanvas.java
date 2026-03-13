package com.airport_sim_2.ui.components;

import java.util.List;

import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Visual runway diagram component that displays runways graphically
 * Shows runway status, mode, and orientation
 */
public class RunwayDiagramCanvas extends Pane {
    
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int RUNWAY_WIDTH = 80;
    private static final int RUNWAY_LENGTH = 300;
    
    // Colors matching design specification
    private static final Color COLOR_BLUE = Color.rgb(59, 130, 246);
    private static final Color COLOR_GREEN = Color.rgb(16, 185, 129);
    private static final Color COLOR_RED = Color.rgb(239, 68, 68);
    private static final Color COLOR_AMBER = Color.rgb(245, 158, 11);
    private static final Color COLOR_PURPLE = Color.rgb(139, 92, 246);
    private static final Color COLOR_SLATE_200 = Color.rgb(226, 232, 240);
    private static final Color COLOR_SLATE_700 = Color.rgb(51, 65, 85);
    
    private Canvas canvas;
    private List<Runway> runways;
    
    public RunwayDiagramCanvas() {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        getChildren().add(canvas);
        
        // Set initial background
        drawBackground();
    }
    
    /**
     * Update the diagram with new runway data
     */
    public void updateRunways(List<Runway> runways) {
        this.runways = runways;
        redraw();
    }
    
    /**
     * Redraw the entire diagram
     */
    private void redraw() {
        drawBackground();
        
        if (runways == null || runways.isEmpty()) {
            drawNoRunwaysMessage();
            return;
        }
        
        // Calculate layout positions
        int cols = (int) Math.ceil(Math.sqrt(runways.size()));
        int rows = (int) Math.ceil((double) runways.size() / cols);
        
        double cellWidth = CANVAS_WIDTH / (double) cols;
        double cellHeight = CANVAS_HEIGHT / (double) rows;
        
        // Draw each runway
        for (int i = 0; i < runways.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            
            double x = col * cellWidth + cellWidth / 2;
            double y = row * cellHeight + cellHeight / 2;
            
            drawRunway(runways.get(i), x, y);
        }
    }
    
    /**
     * Draw background
     */
    private void drawBackground() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Clear canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // Draw grid pattern
        gc.setStroke(COLOR_SLATE_200);
        gc.setLineWidth(1);
        
        for (int i = 0; i < CANVAS_WIDTH; i += 50) {
            gc.strokeLine(i, 0, i, CANVAS_HEIGHT);
        }
        for (int i = 0; i < CANVAS_HEIGHT; i += 50) {
            gc.strokeLine(0, i, CANVAS_WIDTH, i);
        }
    }
    
    /**
     * Draw a single runway
     */
    private void drawRunway(Runway runway, double centerX, double centerY) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Save current state
        gc.save();
        
        // Translate to center
        gc.translate(centerX, centerY);
        
        // Rotate based on bearing (convert to radians)
        double bearing = 90; // Default bearing for visualization
        gc.rotate(bearing);
        
        // Get colors based on status and mode
        Color runwayColor = getRunwayColor(runway);
        Color modeColor = getModeColor(runway.getMode());
        
        // Draw runway rectangle
        gc.setFill(runwayColor);
        gc.fillRect(-RUNWAY_LENGTH/2.0, -RUNWAY_WIDTH/2.0, RUNWAY_LENGTH, RUNWAY_WIDTH);
        
        // Draw runway outline
        gc.setStroke(COLOR_SLATE_700);
        gc.setLineWidth(3);
        gc.strokeRect(-RUNWAY_LENGTH/2.0, -RUNWAY_WIDTH/2.0, RUNWAY_LENGTH, RUNWAY_WIDTH);
        
        // Draw center line
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeLine(-RUNWAY_LENGTH/2.0, 0, RUNWAY_LENGTH/2.0, 0);
        
        // Draw center line dashes
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        for (int i = -RUNWAY_LENGTH/2; i < RUNWAY_LENGTH/2; i += 30) {
            gc.strokeLine(i, 0, i + 15, 0);
        }
        
        // Reset rotation for text
        gc.restore();
        
        // Draw runway number
        gc.setFill(COLOR_SLATE_700);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(Integer.toString(runway.getId()), centerX, centerY - RUNWAY_WIDTH/2.0 - 10);
        
        // Draw mode indicator
        gc.setFill(modeColor);
        gc.fillOval(centerX - 15, centerY + RUNWAY_WIDTH/2.0 + 10, 30, 30);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 10));
        gc.fillText(getModeSymbol(runway.getMode()), 
                   centerX, centerY + RUNWAY_WIDTH/2.0 + 28);
        
        // Draw status text
        gc.setFill(COLOR_SLATE_700);
        gc.setFont(Font.font("System", 10));
        gc.fillText(runway.getStatus().toString(), 
                   centerX, centerY + RUNWAY_WIDTH/2.0 + 50);
    }
    
    /**
     * Get runway color based on status
     */
    private Color getRunwayColor(Runway runway) {
        RunwayOperationalStatus status = runway.getStatus();
        
        if (status == RunwayOperationalStatus.AVAILABLE) {
            return COLOR_GREEN.deriveColor(0, 1, 1, 0.3);
        } else if (status == RunwayOperationalStatus.INSPECTION || 
                   status == RunwayOperationalStatus.SNOW_CLEARANCE) {
            return COLOR_AMBER.deriveColor(0, 1, 1, 0.3);
        } else {
            return COLOR_RED.deriveColor(0, 1, 1, 0.3);
        }
    }
    
    /**
     * Get color for runway mode
     */
    private Color getModeColor(RunwayOpMode mode) {
        switch (mode) {
            case LANDING:
                return COLOR_BLUE;
            case TAKE_OFF:
                return COLOR_GREEN;
            case MIXED_MODE:
                return COLOR_PURPLE;
            default:
                return COLOR_SLATE_700;
        }
    }
    
    /**
     * Get symbol for runway mode
     */
    private String getModeSymbol(RunwayOpMode mode) {
        switch (mode) {
            case LANDING:
                return "↓";
            case TAKE_OFF:
                return "↑";
            case MIXED_MODE:
                return "↕";
            default:
                return "?";
        }
    }
    
    /**
     * Draw message when no runways configured
     */
    private void drawNoRunwaysMessage() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.setFill(COLOR_SLATE_700);
        gc.setFont(Font.font("System", 16));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("No runways configured", CANVAS_WIDTH / 2.0, CANVAS_HEIGHT / 2.0);
        
        gc.setFont(Font.font("System", 12));
        gc.fillText("Add runways in the configuration panel above", 
                   CANVAS_WIDTH / 2.0, CANVAS_HEIGHT / 2.0 + 25);
    }
    
    /**
     * Get the canvas for embedding in layouts
     */
    public Canvas getCanvas() {
        return canvas;
    }
}
