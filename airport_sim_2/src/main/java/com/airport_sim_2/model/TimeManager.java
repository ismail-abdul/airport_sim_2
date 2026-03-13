package com.airport_sim_2.model;

public class TimeManager {
    private boolean paused = true;
    private double timeScale = 1.0;

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void setTimeScale(double scale) {
        this.timeScale = scale;
    }

    public boolean isPaused() {
        return paused;
    }

    public double getTimeScale() {
        return timeScale;
    }
}
