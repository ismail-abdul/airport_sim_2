package com.airport_sim_2.controller;
import java.util.ArrayList;
import java.util.List;

public class StatisticsCollector {
    private int maxHoldingSize = 0;
    private int maxTakeOffQueueSize = 0;
    private int cancelledCount = 0;
    private int divertedCount = 0;
    private List<Double> arrivalWaitTimes = new ArrayList<>();
    private List<Double> departureWaitTimes = new ArrayList<>();
    private double maxWaitTimeRecorded = 0;

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
}
