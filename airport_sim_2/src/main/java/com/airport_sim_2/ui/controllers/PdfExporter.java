package com.airport_sim_2.ui.controllers;

import com.airport_sim_2.controller.StatisticsCollector;
import com.airport_sim_2.controller.TimeSeriesPoint;
import com.airport_sim_2.model.SimulationContext;
import com.airport_sim_2.model.SimulationEngine;
import com.airport_sim_2.objects.Runway;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates a two-page PDF report from the completed simulation results.
 *
 * Page 1 – summary statistics and runway performance table.
 * Page 2 – all three charts, freshly rendered at high resolution, with
 *           descriptions and legends, laid out to fill the page.
 */
public class PdfExporter {

    // A4 dimensions in PDF points (1pt = 1/72 inch)
    private static final float PAGE_W    = PDRectangle.A4.getWidth();   // 595.28
    private static final float PAGE_H    = PDRectangle.A4.getHeight();  // 841.89
    private static final float MARGIN    = 50f;
    private static final float CONTENT_W = PAGE_W - 2 * MARGIN;

    // Resolution at which each chart is rendered off-screen before embedding
    private static final int CHART_PX_W = 1400;
    private static final int CHART_PX_H = 420;

    // Typography for the charts page
    private static final float DESC_FONT_SIZE  = 18f;
    private static final float DESC_LINE_H     = 23f;   // leading for DESC_FONT_SIZE
    private static final float CHART_TITLE_SIZE = 14f;

    // Fonts (PDFBox standard Type-1)
    private static final PDType1Font FONT_BOLD    = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font FONT_REGULAR = PDType1Font.HELVETICA;

    private final SimulationEngine  engine;
    private final SimulationContext context;

    // The live on-screen chart references are kept only for API compatibility;
    // the PDF now renders fresh off-screen copies at higher resolution.
    @SuppressWarnings("unused")
    private final LineChart<Number, Number> queueLengthChart;
    @SuppressWarnings("unused")
    private final BarChart<String, Number>  delayDistributionChart;
    @SuppressWarnings("unused")
    private final LineChart<Number, Number> throughputChart;

    public PdfExporter(SimulationEngine engine,
                       SimulationContext context,
                       LineChart<Number, Number> queueLengthChart,
                       BarChart<String, Number>  delayDistributionChart,
                       LineChart<Number, Number> throughputChart) {
        this.engine                 = engine;
        this.context                = context;
        this.queueLengthChart       = queueLengthChart;
        this.delayDistributionChart = delayDistributionChart;
        this.throughputChart        = throughputChart;
    }

    /** Entry point – writes the PDF to {@code destFile}. */
    public void export(File destFile) throws IOException {
        StatisticsCollector stats = context.getStatistics();

        // Build fresh, large off-screen charts so they fill the page and
        // data points are evenly distributed across the available space.
        BufferedImage queueImg      = snapshotOffscreen(buildQueueChart(),      CHART_PX_W, CHART_PX_H);
        BufferedImage delayImg      = snapshotOffscreen(buildDelayChart(),      CHART_PX_W, CHART_PX_H);
        BufferedImage throughputImg = snapshotOffscreen(buildThroughputChart(), CHART_PX_W, CHART_PX_H);

        try (PDDocument doc = new PDDocument()) {
            writePage1(doc, stats);
            writeChartsPage(doc, queueImg, delayImg, throughputImg);
            doc.save(destFile);
        }
    }

    // -----------------------------------------------------------------------
    // Chart builders – create standalone chart nodes with the simulation data
    // -----------------------------------------------------------------------

    private LineChart<Number, Number> buildQueueChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (minutes)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Aircraft");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);

        StatisticsCollector stats = context.getStatistics();

        XYChart.Series<Number, Number> holding = new XYChart.Series<>();
        holding.setName("Holding Pattern");
        for (TimeSeriesPoint p : stats.getHp_time_series())
            holding.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));

        XYChart.Series<Number, Number> takeoff = new XYChart.Series<>();
        takeoff.setName("Takeoff Queue");
        for (TimeSeriesPoint p : stats.getToq_time_series())
            takeoff.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));

        chart.getData().addAll(holding, takeoff);
        return chart;
    }

    private BarChart<String, Number> buildDelayChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Delay Range (minutes)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Aircraft Count");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setLegendVisible(true);

        StatisticsCollector stats = context.getStatistics();

        List<Double> allWaits = new ArrayList<>();
        allWaits.addAll(stats.getArrivalWaitTimes());
        allWaits.addAll(stats.getDepartureWaitTimes());
        if (allWaits.isEmpty()) return chart;

        double mean    = allWaits.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = allWaits.stream().mapToDouble(w -> (w - mean) * (w - mean)).average().orElse(0);
        double stdDev  = Math.sqrt(variance);
        int    numBins = 8;
        double dataMin = allWaits.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double dataMax = allWaits.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double sigma   = Math.max(stdDev, (dataMax - dataMin) / numBins);
        double rangeMin = Math.max(0, Math.min(dataMin, mean - 3 * sigma));
        double rangeMax = Math.max(dataMax, mean + 3 * sigma);
        double binWidth = (rangeMax - rangeMin) / numBins;
        if (binWidth <= 0) binWidth = 1;

        int[] counts = new int[numBins];
        for (double w : allWaits) {
            int idx = w < rangeMin ? 0
                    : w >= rangeMax ? numBins - 1
                    : Math.min((int) ((w - rangeMin) / binWidth), numBins - 1);
            counts[idx]++;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Wait Time Distribution");
        for (int i = 0; i < numBins; i++) {
            double lo = rangeMin + i * binWidth;
            double hi = lo + binWidth;
            String label = (i == numBins - 1)
                    ? String.format("%.1f+", lo)
                    : String.format("%.1f-%.1f", lo, hi);
            series.getData().add(new XYChart.Data<>(label, counts[i]));
        }
        chart.getData().add(series);

        int maxCount = Arrays.stream(counts).max().orElse(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxCount * 1.25);
        yAxis.setTickUnit(Math.max(1, Math.round(maxCount / 5.0)));

        return chart;
    }

    private LineChart<Number, Number> buildThroughputChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (minutes)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Aircraft");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);

        StatisticsCollector stats = context.getStatistics();

        XYChart.Series<Number, Number> arrivals = new XYChart.Series<>();
        arrivals.setName("Cumulative Arrivals");
        for (TimeSeriesPoint p : stats.getArrival_time_series())
            arrivals.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));

        XYChart.Series<Number, Number> departures = new XYChart.Series<>();
        departures.setName("Cumulative Departures");
        for (TimeSeriesPoint p : stats.getDeparture_time_series())
            departures.getData().add(new XYChart.Data<>(p.getTime() / 60.0, p.getValue()));

        chart.getData().addAll(arrivals, departures);
        return chart;
    }

    // -----------------------------------------------------------------------
    // Page 1 – statistics
    // -----------------------------------------------------------------------

    private void writePage1(PDDocument doc, StatisticsCollector stats) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float y = PAGE_H - MARGIN;

            y = drawTitle(cs, y, "Airport Simulation Report");
            y = drawSubtitle(cs, y,
                    "Generated: " + LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            y -= 12;
            y = drawSectionHeading(cs, y, "Simulation Summary");
            y -= 6;
            y = drawSummaryTable(cs, y, stats);
            y -= 18;
            y = drawSectionHeading(cs, y, "Wait Time Breakdown");
            y -= 6;
            y = drawWaitTimeTable(cs, y, stats);
            y -= 18;
            y = drawSectionHeading(cs, y, "Runway Performance");
            y -= 6;
            drawRunwayTable(cs, y);
        }
    }

    private float drawTitle(PDPageContentStream cs, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(FONT_BOLD, 20);
        cs.newLineAtOffset(MARGIN, y - 20);
        cs.showText(text);
        cs.endText();
        return y - 30;
    }

    private float drawSubtitle(PDPageContentStream cs, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(FONT_REGULAR, 10);
        cs.newLineAtOffset(MARGIN, y - 12);
        cs.showText(text);
        cs.endText();
        return y - 18;
    }

    private float drawSectionHeading(PDPageContentStream cs, float y, String text) throws IOException {
        cs.setLineWidth(0.8f);
        cs.moveTo(MARGIN, y - 2);
        cs.lineTo(PAGE_W - MARGIN, y - 2);
        cs.stroke();

        cs.beginText();
        cs.setFont(FONT_BOLD, 13);
        cs.newLineAtOffset(MARGIN, y - 14);
        cs.showText(text);
        cs.endText();
        return y - 20;
    }

    /** 2-column table: label | value */
    private float drawSummaryTable(PDPageContentStream cs, float y,
                                   StatisticsCollector stats) throws IOException {
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

        int maxQueue = Math.max(stats.getMaxHoldingSize(), stats.getMaxTakeoffQueueSize());

        String[][] rows = {
            { "Total Aircraft Processed", String.valueOf(total) },
            { "Average Wait Time (Overall)", String.format("%.1f min", avgWait) },
            { "Max Queue Length", String.valueOf(maxQueue) },
            { "Diversions", String.valueOf(stats.getDivertedCount()) },
            { "Cancellations", String.valueOf(stats.getCancelledCount()) },
            { "Runway Utilisation", utilStr },
        };

        return drawTable(cs, y, new String[]{ "Metric", "Value" }, rows,
                         new float[]{ CONTENT_W * 0.65f, CONTENT_W * 0.35f });
    }

    private float drawWaitTimeTable(PDPageContentStream cs, float y,
                                    StatisticsCollector stats) throws IOException {
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

        String[][] rows = {
            { "Avg Arrival Wait Time",          String.format("%.2f min", avgArr) },
            { "Avg Departure Wait Time",         String.format("%.2f min", avgDep) },
            { "Combined Avg Wait Time",          String.format("%.2f min", avgComb) },
            { "Max Individual Wait Time",        String.format("%.2f min", maxWait) },
            { "Avg Queue Wait (time-weighted)",  String.format("%.2f min", avgQueueWait) },
        };

        return drawTable(cs, y, new String[]{ "Metric", "Value" }, rows,
                         new float[]{ CONTENT_W * 0.65f, CONTENT_W * 0.35f });
    }

    private float drawRunwayTable(PDPageContentStream cs, float y) throws IOException {
        if (context.getRunways() == null || context.getRunways().isEmpty()) return y;

        double totalSimMinutes = engine.getEndTime() / 60.0;
        double avgOp = (context.getLandingDuration() + context.getTakeOffDuration()) / 2.0;

        String[][] rows = new String[context.getRunways().size()][4];
        int i = 0;
        for (Runway r : context.getRunways()) {
            int ops = r.getOperationCount();
            double util = totalSimMinutes > 0
                    ? Math.min(ops * avgOp / totalSimMinutes * 100.0, 100.0) : 0;
            rows[i++] = new String[]{
                String.valueOf(r.getId()),
                r.getMode().toString(),
                String.valueOf(ops),
                String.format("%.1f%%", util)
            };
        }

        return drawTable(cs, y,
                new String[]{ "Runway", "Mode", "Total Ops", "Utilisation %" },
                rows,
                new float[]{
                    CONTENT_W * 0.15f,
                    CONTENT_W * 0.35f,
                    CONTENT_W * 0.25f,
                    CONTENT_W * 0.25f
                });
    }

    // -----------------------------------------------------------------------
    // Page 2 – all three charts with descriptions
    // -----------------------------------------------------------------------

    private void writeChartsPage(PDDocument doc,
                                  BufferedImage queueImg,
                                  BufferedImage delayImg,
                                  BufferedImage throughputImg) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        final String[] titles = {
            "Queue Length Over Time",
            "Delay Distribution",
            "Cumulative Throughput"
        };
        final String[] descriptions = {
            "Number of aircraft waiting in the holding pattern (arrivals) and the takeoff queue " +
            "(departures) over time. Spikes indicate periods of high congestion at the airport.",
            "Distribution of aircraft wait times across equal-width delay bins (arrivals and " +
            "departures combined). Taller bars indicate more aircraft experienced that delay range.",
            "Running total of aircraft that have successfully landed (arrivals) and departed " +
            "(departures). A widening gap between the lines signals a traffic flow imbalance."
        };
        final BufferedImage[] images = { queueImg, delayImg, throughputImg };

        // Pre-wrap all descriptions to know how much vertical space they need
        List<List<String>> wrappedDescs = new ArrayList<>();
        for (String desc : descriptions)
            wrappedDescs.add(wrapText(desc, FONT_REGULAR, DESC_FONT_SIZE, CONTENT_W));

        // Fixed vertical costs per chart block (title + gaps around desc + gap after image)
        final float TITLE_BLOCK   = CHART_TITLE_SIZE + 5f;  // title text + gap below
        final float GAP_AFTER_DESC = 8f;
        final float GAP_AFTER_IMG  = 16f;
        final float HEADING_BLOCK  = 30f;  // drawSectionHeading + extra gap

        float totalDescH = 0;
        for (List<String> lines : wrappedDescs)
            totalDescH += lines.size() * DESC_LINE_H;

        float totalOverhead = HEADING_BLOCK
                + 3 * (TITLE_BLOCK + GAP_AFTER_DESC + GAP_AFTER_IMG)
                + totalDescH;

        float availableForImages = (PAGE_H - 2 * MARGIN) - totalOverhead;
        float imageH = Math.max(60f, availableForImages / 3f); // min 60pt safety floor
        float imageW = CONTENT_W;

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            float y = PAGE_H - MARGIN;
            y = drawSectionHeading(cs, y, "Performance Charts");
            y -= 10;

            for (int i = 0; i < 3; i++) {
                // Chart title
                cs.beginText();
                cs.setFont(FONT_BOLD, CHART_TITLE_SIZE);
                cs.newLineAtOffset(MARGIN, y - CHART_TITLE_SIZE);
                cs.showText(titles[i]);
                cs.endText();
                y -= TITLE_BLOCK;

                // Description at 18pt, word-wrapped
                for (String line : wrappedDescs.get(i)) {
                    cs.beginText();
                    cs.setFont(FONT_REGULAR, DESC_FONT_SIZE);
                    cs.newLineAtOffset(MARGIN, y - DESC_FONT_SIZE);
                    cs.showText(line);
                    cs.endText();
                    y -= DESC_LINE_H;
                }
                y -= GAP_AFTER_DESC;

                // Chart image scaled to fill computed height
                if (images[i] != null)
                    drawImage(doc, cs, images[i], MARGIN, y - imageH, imageW, imageH);

                y -= imageH + GAP_AFTER_IMG;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Shared drawing helpers
    // -----------------------------------------------------------------------

    private float drawTable(PDPageContentStream cs,
                             float y,
                             String[] headers,
                             String[][] rows,
                             float[] colWidths) throws IOException {
        final float ROW_H    = 18f;
        final float CELL_PAD = 5f;
        final float tableW   = sumFloats(colWidths);

        // Header row background
        cs.setNonStrokingColor(0.85f, 0.85f, 0.85f);
        cs.addRect(MARGIN, y - ROW_H, tableW, ROW_H);
        cs.fill();
        cs.setNonStrokingColor(0f, 0f, 0f);

        float cx = MARGIN;
        for (int c = 0; c < headers.length; c++) {
            cs.beginText();
            cs.setFont(FONT_BOLD, 9);
            cs.newLineAtOffset(cx + CELL_PAD, y - ROW_H + 5);
            cs.showText(headers[c]);
            cs.endText();
            cx += colWidths[c];
        }
        y -= ROW_H;

        for (int r = 0; r < rows.length; r++) {
            if (r % 2 == 1) {
                cs.setNonStrokingColor(0.95f, 0.95f, 0.95f);
                cs.addRect(MARGIN, y - ROW_H, tableW, ROW_H);
                cs.fill();
                cs.setNonStrokingColor(0f, 0f, 0f);
            }
            cx = MARGIN;
            for (int c = 0; c < rows[r].length; c++) {
                cs.beginText();
                cs.setFont(FONT_REGULAR, 9);
                cs.newLineAtOffset(cx + CELL_PAD, y - ROW_H + 5);
                cs.showText(rows[r][c] != null ? rows[r][c] : "");
                cs.endText();
                cx += colWidths[c];
            }
            y -= ROW_H;
        }

        cs.setStrokingColor(0.5f, 0.5f, 0.5f);
        cs.setLineWidth(0.5f);
        cs.addRect(MARGIN, y, tableW, ROW_H * (rows.length + 1));
        cs.stroke();
        cs.setStrokingColor(0f, 0f, 0f);

        return y - 4;
    }

    private void drawImage(PDDocument doc,
                           PDPageContentStream cs,
                           BufferedImage img,
                           float x, float y,
                           float w, float h) throws IOException {
        if (img == null) return;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        PDImageXObject pdImg = PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "chart");
        cs.drawImage(pdImg, x, y, w, h);
    }

    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------

    /**
     * Renders a JavaFX chart node to a {@link BufferedImage} at the requested
     * pixel dimensions. The chart is placed in a temporary Scene so that CSS
     * and layout are fully applied before snapshotting.
     */
    private static BufferedImage snapshotOffscreen(javafx.scene.chart.Chart chart, int w, int h) {
        chart.setPrefSize(w, h);
        // Placing the chart in a Scene triggers CSS application and a layout pass
        new javafx.scene.Scene(chart, w, h);
        chart.applyCss();
        chart.layout();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.WHITE);
        WritableImage wi = chart.snapshot(params, new WritableImage(w, h));
        return SwingFXUtils.fromFXImage(wi, null);
    }

    /**
     * Word-wraps {@code text} so that no line exceeds {@code maxWidth} PDF points
     * when rendered in {@code font} at {@code fontSize}.
     */
    private static List<String> wrapText(String text,
                                          PDType1Font font,
                                          float fontSize,
                                          float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            float lineWidth = font.getStringWidth(candidate) / 1000f * fontSize;
            if (lineWidth > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (current.length() > 0)
            lines.add(current.toString());

        return lines;
    }

    private static float sumFloats(float[] arr) {
        float s = 0;
        for (float v : arr) s += v;
        return s;
    }
}
