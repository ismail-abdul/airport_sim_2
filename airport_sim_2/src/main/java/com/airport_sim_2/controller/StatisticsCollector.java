package com.airport_sim_2.controller;
import java.util.ArrayList;
import java.util.List;

public class StatisticsCollector {
    private int maxHoldingSize = 0;
    private int maxTakeOffQueueSize = 0;
    private int cancelledCount = 0;
    private int divertedCount = 0;
    private int arrivedCount = 0;
    private int departedCount = 0;
    private List<Double> arrivalWaitTimes = new ArrayList<>();
    private List<Double> departureWaitTimes = new ArrayList<>();
    private double maxWaitTimeRecorded = 0;

    // Time series data
    private List<TimeSeriesPoint> diversion_time_series = new ArrayList<>();
    private List<TimeSeriesPoint> cancellation_time_series = new ArrayList<>();
    private List<TimeSeriesPoint> delay_time_series = new ArrayList<>();
    private List<TimeSeriesPoint> hp_time_series = new ArrayList<>();
    private List<TimeSeriesPoint> toq_time_series = new ArrayList<>();

    // Write a constructor for this.

    // Diversion time series - add a point
    public void diversion_ts_add(TimeSeriesPoint p) {
        diversion_time_series.add(p);
    }

    // Cancellation time series - add a point
    public void cancellation_ts_add(TimeSeriesPoint p) {
        cancellation_time_series.add(p);
    }

    // Holding pattern time series - add a point
    public void hp_ts_add(TimeSeriesPoint p) {
        hp_time_series.add(p);
    }

    // Take Off queue additoin
    public void toq_ts_add(TimeSeriesPoint p) {
        toq_time_series.add(p);
    }

    public void delay_ts_add(TimeSeriesPoint p) {
        delay_time_series.add(p);
    }

    public void updateMaxHoldingSize(int currentSize) {
        if (currentSize > maxHoldingSize) {
            maxHoldingSize = currentSize;
        }
    }

    public void updateMaxTakeoffQueueSize(int currentSize) {
        if (currentSize > maxTakeOffQueueSize) {
            maxTakeOffQueueSize = currentSize;
        }
    }

    public void recordArrivalWait(double waitTime) {
        arrivalWaitTimes.add(waitTime);
        updateMaxWait(waitTime);
    }

    public void recordDepartureWait(double waitTime) {
        departureWaitTimes.add(waitTime);
        updateMaxWait(waitTime);
    }

    private void updateMaxWait(double waitTime) {
        if (waitTime > maxWaitTimeRecorded) {
            maxWaitTimeRecorded = waitTime;
        }
    }

    public void incrementCancelled() {
        cancelledCount++;
    }

    public void incrementDiverted() {
        divertedCount++;
    }

    public void incrementArrived() {
        arrivedCount++;
    }

    public void incrementDeparted() {
        departedCount++;
    }

    public int getArrivedCount() {
        return arrivedCount;
    }

    public int getDepartedCount() {
        return departedCount;
    }

    public void addDiverted(int number_of_diverted) {
        divertedCount += number_of_diverted;
    }

    public double getAverageArrivalWait() {
        return arrivalWaitTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public double getAverageDepartureWait() {
        return departureWaitTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public int getMaxHoldingSize() {
        return maxHoldingSize;
    }

    public int getMaxTakeoffQueueSize() {
        return maxTakeOffQueueSize;
    }

    public double getMaxWaitTimeRecorded() {
        return maxWaitTimeRecorded;
    }

    public int getCancelledCount() {
        return cancelledCount;
    }

    public int getDivertedCount() {
        return divertedCount;
    }

    public List<TimeSeriesPoint> getDelay_time_series() {
        return delay_time_series;
    }

    public List<TimeSeriesPoint> getHp_time_series() {
        return hp_time_series;
    }

    public List<TimeSeriesPoint> getToq_time_series() {
        return toq_time_series;
    }

    public List<TimeSeriesPoint> getDiversion_time_series() {
        return diversion_time_series;
    }

    public List<TimeSeriesPoint> getCancellation_time_series() {
        return cancellation_time_series;
    }

    public List<Double> getArrivalWaitTimes() {
        return arrivalWaitTimes;
    }

    public List<Double> getDepartureWaitTimes() {
        return departureWaitTimes;
    }

    // Arrival time series
    private List<TimeSeriesPoint> arrival_time_series = new ArrayList<>();

    public void arrival_ts_add(TimeSeriesPoint p) {
        arrival_time_series.add(p);
    }

    public List<TimeSeriesPoint> getArrival_time_series() {
        return arrival_time_series;
    }

    // Departure time series
    private List<TimeSeriesPoint> departure_time_series = new ArrayList<>();

    public void departure_ts_add(TimeSeriesPoint p) {
        departure_time_series.add(p);
    }

    public List<TimeSeriesPoint> getDeparture_time_series() {
        return departure_time_series;
    }
}




// diversions over time
// cancellations over time
// HoldingPattern size over time
// TakeOff queue size over time
// Average wait time
// Number of planes over time
// Failure rate
