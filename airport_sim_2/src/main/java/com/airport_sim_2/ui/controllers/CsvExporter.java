package com.airport_sim_2.ui.controllers;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.controller.TimeSeriesPoint;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exports simulation results to a CSV file.
 *
 * The file uses multiple labelled sections separated by blank lines so it
 * can be opened directly in Excel or any spreadsheet application.
 */
public class CsvExporter {

    private final SimulationEngine  engine;
    private final SimulationContext context;

    public CsvExporter(SimulationEngine engine, SimulationContext context) {
        this.engine  = engine;
        this.context = context;
    }

    public void export(File destFile) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(destFile))) {
            StatisticsCollector stats = context.getStatistics();
            writeSummary(pw, stats);
            writeWaitTimeBreakdown(pw, stats);
            writeRunwayPerformance(pw);
            writeIndividualWaitTimes(pw, stats);
            writeTimeSeries(pw, stats);
        }
    }

    // -----------------------------------------------------------------------
    // Sections
    // -----------------------------------------------------------------------

    private void writeSummary(PrintWriter pw, StatisticsCollector stats) {
        int total = stats.getArrivedCount() + stats.getDepartedCount();

        int    arrCount = stats.getArrivalWaitTimes().size();
        int    depCount = stats.getDepartureWaitTimes().size();
        double avgWait  = 0;
        if (arrCount + depCount > 0) {
            avgWait = (stats.getAverageArrivalWait() * arrCount
                     + stats.getAverageDepartureWait() * depCount)
                     / (arrCount + depCount);
        }

        double totalSimMinutes = engine.getEndTime() / 60.0;
        int    numRunways = context.getRunways() != null ? context.getRunways().size() : 1;
        String utilStr;
        if (totalSimMinutes > 0 && numRunways > 0) {
            double occ = stats.getArrivedCount()  * context.getLandingDuration()
                       + stats.getDepartedCount() * context.getTakeOffDuration();
            double u = occ / (totalSimMinutes * numRunways) * 100.0;
            utilStr = String.format("%.1f%%", Math.min(u, 100.0));
        } else {
            utilStr = "N/A";
        }

        pw.println("SIMULATION SUMMARY");
        pw.println("Generated," + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        pw.println();
        pw.println("Metric,Value");
        pw.println("Total Aircraft Processed," + total);
        pw.println("Total Arrivals," + stats.getArrivedCount());
        pw.println("Total Departures," + stats.getDepartedCount());
        pw.printf("Average Wait Time (Overall),%.2f min%n", avgWait);
        pw.println("Max Queue Length," + Math.max(stats.getMaxHoldingSize(), stats.getMaxTakeoffQueueSize()));
        pw.println("Diversions," + stats.getDivertedCount());
        pw.println("Cancellations," + stats.getCancelledCount());
        pw.println("Runway Utilisation," + utilStr);
        pw.println();
    }

    private void writeWaitTimeBreakdown(PrintWriter pw, StatisticsCollector stats) {
        int    arrCount = stats.getArrivalWaitTimes().size();
        int    depCount = stats.getDepartureWaitTimes().size();
        double avgArr   = stats.getAverageArrivalWait();
        double avgDep   = stats.getAverageDepartureWait();
        double avgComb  = 0;
        if (arrCount + depCount > 0)
            avgComb = (avgArr * arrCount + avgDep * depCount) / (arrCount + depCount);

        double maxWait = Math.max(
                stats.getArrivalWaitTimes().stream().mapToDouble(Double::doubleValue).max().orElse(0),
                stats.getDepartureWaitTimes().stream().mapToDouble(Double::doubleValue).max().orElse(0));

        // Time-weighted average queue wait
        double avgQueueWait = 0;
        List<TimeSeriesPoint> hpSeries = stats.getHp_time_series();
        if (hpSeries.size() >= 2 && arrCount > 0) {
            double weightedSum = 0, totalTime = 0;
            for (int i = 1; i < hpSeries.size(); i++) {
                double dt = hpSeries.get(i).getTime() - hpSeries.get(i - 1).getTime();
                weightedSum += hpSeries.get(i - 1).getValue() * dt;
                totalTime   += dt;
            }
            avgQueueWait = totalTime > 0 ? (weightedSum / totalTime) / 60.0 : 0;
        }

        pw.println("WAIT TIME BREAKDOWN");
        pw.println("Metric,Value");
        pw.printf("Avg Arrival Wait Time,%.2f min%n", avgArr);
        pw.printf("Avg Departure Wait Time,%.2f min%n", avgDep);
        pw.printf("Combined Avg Wait Time,%.2f min%n", avgComb);
        pw.printf("Max Individual Wait Time,%.2f min%n", maxWait);
        pw.printf("Avg Queue Wait (time-weighted),%.2f min%n", avgQueueWait);
        pw.println();
    }

    private void writeRunwayPerformance(PrintWriter pw) {
        if (context.getRunways() == null || context.getRunways().isEmpty()) return;

        double totalSimMinutes = engine.getEndTime() / 60.0;
        double avgOp = (context.getLandingDuration() + context.getTakeOffDuration()) / 2.0;

        pw.println("RUNWAY PERFORMANCE");
        pw.println("Runway,Mode,Total Operations,Utilisation %");
        for (Runway r : context.getRunways()) {
            int ops = r.getOperationCount();
            double util = totalSimMinutes > 0
                    ? Math.min(ops * avgOp / totalSimMinutes * 100.0, 100.0) : 0;
            pw.printf("%d,%s,%d,%.1f%%%n",
                    r.getId(), r.getMode(), ops, util);
        }
        pw.println();
    }

    private void writeIndividualWaitTimes(PrintWriter pw, StatisticsCollector stats) {
        List<Double> arrWaits = stats.getArrivalWaitTimes();
        List<Double> depWaits = stats.getDepartureWaitTimes();

        pw.println("INDIVIDUAL WAIT TIMES");
        pw.println("Type,Wait Time (min)");
        for (double w : arrWaits)
            pw.printf("Arrival,%.2f%n", w);
        for (double w : depWaits)
            pw.printf("Departure,%.2f%n", w);
        pw.println();
    }

    private void writeTimeSeries(PrintWriter pw, StatisticsCollector stats) {
        writeSeriesSection(pw, "HOLDING PATTERN QUEUE OVER TIME",
                stats.getHp_time_series());
        writeSeriesSection(pw, "TAKEOFF QUEUE OVER TIME",
                stats.getToq_time_series());
        writeSeriesSection(pw, "CUMULATIVE ARRIVALS OVER TIME",
                stats.getArrival_time_series());
        writeSeriesSection(pw, "CUMULATIVE DEPARTURES OVER TIME",
                stats.getDeparture_time_series());
        writeSeriesSection(pw, "DIVERSIONS OVER TIME",
                stats.getDiversion_time_series());
        writeSeriesSection(pw, "CANCELLATIONS OVER TIME",
                stats.getCancellation_time_series());
    }

    private void writeSeriesSection(PrintWriter pw,
                                     String heading,
                                     List<TimeSeriesPoint> series) {
        pw.println(heading);
        pw.println("Time (min),Value");
        for (TimeSeriesPoint p : series)
            pw.printf("%.3f,%.2f%n", p.getTime() / 60.0, p.getValue());
        pw.println();
    }
}
