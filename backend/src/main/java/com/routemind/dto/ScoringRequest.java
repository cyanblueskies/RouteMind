package com.routemind.dto;

import java.util.List;

//Request body for the scoring API endpoint./
public class ScoringRequest {
    private List<Point> points;
    private int noiseWeight;
    private int lightingWeight;
    private int pollutionWeight;
    private int wheelchairWeight;

    public ScoringRequest() {}

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public int getNoiseWeight() {
        return noiseWeight;
    }

    public void setNoiseWeight(int noiseWeight) {
        this.noiseWeight = noiseWeight;
    }

    public int getLightingWeight() {
        return lightingWeight;
    }

    public void setLightingWeight(int lightingWeight) {
        this.lightingWeight = lightingWeight;
    }

    public int getPollutionWeight() {
        return pollutionWeight;
    }

    public void setPollutionWeight(int pollutionWeight) {
        this.pollutionWeight = pollutionWeight;
    }

    public int getWheelchairWeight() {
        return wheelchairWeight;
    }

    public void setWheelchairWeight(int wheelchairWeight) {
        this.wheelchairWeight = wheelchairWeight;
    }
}
