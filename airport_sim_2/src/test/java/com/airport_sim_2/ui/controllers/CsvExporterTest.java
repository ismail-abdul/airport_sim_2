package com.airport_sim_2.ui.controllers;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;
import com.airport_sim_2.objects.RunwayOpMode;
import com.airport_sim_2.objects.RunwayOperationalStatus;
import com.airport_sim_2.queues.HoldingPattern;
import com.airport_sim_2.queues.TakeOffQueue;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for Data Export functionality.
 * Proves that the system successfully generates CSV reports without crashing
 * and formats the data correctly
 */
public class CsvExporterTest {

    @Test
    public void testCsvExportCreatesValidFile() throws Exception {
        //Setup Mock Environment with required dependencies
        HoldingPattern hp = new HoldingPattern();
        TakeOffQueue toq = new TakeOffQueue();
        
        //Add at least one dummy runway so the Exporter prints the "RUNWAY PERFORMANCE" section
        List<Runway> runways = new ArrayList<>();
        runways.add(new Runway(1, RunwayOpMode.MIXED_MODE, RunwayOperationalStatus.AVAILABLE)); 
        
        StatisticsCollector stats = new StatisticsCollector();
        
        SimulationContext context = new SimulationContext(hp, toq, runways, stats);
        SimulationEngine engine = new SimulationEngine(120.0, context); // 120 minutes

        //Add some fake statistics to prove it writes data
        stats.incrementArrived();
        stats.recordArrivalWait(10.5);
        stats.incrementCancelled();

        CsvExporter exporter = new CsvExporter(engine, context);

        //Create a temporary file
        File tempFile = File.createTempFile("test_simulation_report", ".csv");
        tempFile.deleteOnExit(); 

        //Run the export method
        exporter.export(tempFile);

        //ASSERT: Verify the file was physically created and has content
        assertTrue("The CSV file should have been created on the system.", tempFile.exists());
        assertTrue("The CSV file should not be empty.", tempFile.length() > 0);

        //Read all the text from the newly created file
        List<String> lines = Files.readAllLines(tempFile.toPath());
        String fileContent = String.join("\n", lines);

        //ASSERT: Verify the formatting and sections are correct
        assertTrue("Should contain Summary header", fileContent.contains("SIMULATION SUMMARY"));
        assertTrue("Should contain Wait Time header", fileContent.contains("WAIT TIME BREAKDOWN"));
        assertTrue("Should contain Runway header", fileContent.contains("RUNWAY PERFORMANCE"));
        assertTrue("Should contain Time Series header", fileContent.contains("HOLDING PATTERN QUEUE OVER TIME"));

        //Verify the fake data was accurately translated to the file
        assertTrue("Should log the 1 arrival we faked", fileContent.contains("Total Arrivals,1"));
        assertTrue("Should log the 1 cancellation we faked", fileContent.contains("Cancellations,1"));
    }
}