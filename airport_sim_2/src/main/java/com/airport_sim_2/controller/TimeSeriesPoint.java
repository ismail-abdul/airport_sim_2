package com.airport_sim_2.controller;


public class TimeSeriesPoint {
    private final double time;
    private final double value;

    public TimeSeriesPoint(double t, double v) {
        this.time = t;
        this.value = v;
    }

    public double getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }
}