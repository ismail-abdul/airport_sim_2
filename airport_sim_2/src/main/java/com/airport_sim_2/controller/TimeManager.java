package com.airport_sim_2.controller;

public class TimeManager {

    private double internalTimeSeconds = 0.0;
    private double timeScale = 1.0;
    private boolean paused = false;

    private long lastRealTime;

    public TimeManager() {
        lastRealTime = System.nanoTime();
    }

    public void update() {
        long now = System.nanoTime();
        double realSecondsPassed = (now - lastRealTime) / 1_000_000_000.0; //Dividing to convert nanoseconds to seconds
        lastRealTime = now;

        if (!paused) {
            internalTimeSeconds += realSecondsPassed * timeScale;
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
        lastRealTime = System.nanoTime(); 
    }

    public void setTimeScale(double scale) {
        this.timeScale = scale;
    }

    public double getinternalTimeSeconds() {
        return internalTimeSeconds;
    }

}
